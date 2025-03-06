package com.example.courtconnect3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminConfirmActivity extends AppCompatActivity {

    private LinearLayout requestsLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_confirm);

        // התחברות ל-Firestore
        db = FirebaseFirestore.getInstance();
        requestsLayout = findViewById(R.id.requests_layout);

        // טעינת בקשות
        loadRequests();
    }

    private void loadRequests() {
        db.collection("courtRequests")
                .whereEqualTo("status", "pending") // מביא רק בקשות שממתינות לאישור
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String requestId = document.getId();
                        String name = document.getString("name");
                        String address = document.getString("address");
                        String hours = document.getString("hours");
                        Map<String, Object> location = (Map<String, Object>) document.get("location");
                        double latitude = (double) location.get("latitude");
                        double longitude = (double) location.get("longitude");

                        // יצירת תצוגה עבור כל בקשה
                        TextView requestView = new TextView(this);
                        requestView.setText("שם: " + name + "\nכתובת: " + address + "\nשעות: " + hours +
                                "\nמיקום: " + latitude + ", " + longitude);
                        requestView.setPadding(16, 16, 16, 16);

                        Button approveButton = new Button(this);
                        approveButton.setText("אישור");
                        approveButton.setOnClickListener(view -> {
                            approveRequest(requestId, name, address, hours, latitude, longitude);
                            requestsLayout.removeView(requestView);
                            requestsLayout.removeView(approveButton);
                        });

                        requestsLayout.addView(requestView);
                        requestsLayout.addView(approveButton);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בטעינת הבקשות", Toast.LENGTH_SHORT).show();
                });
    }

    private void approveRequest(String requestId, String name, String address, String hours, double latitude, double longitude) {
        // יצירת מידע המגרש
        Map<String, Object> field = new HashMap<>();
        field.put("name", name);
        field.put("address", address);
        field.put("hours", hours);
        field.put("location", new HashMap<String, Object>() {{
            put("latitude", latitude);
            put("longitude", longitude);
        }});

        // שמירה בקולקציה של מגרשים מאושרים
        db.collection("fields")
                .add(field)
                .addOnSuccessListener(documentReference -> {
                    // עדכון הסטטוס של הבקשה ל-"approved"
                    db.collection("fieldRequests").document(requestId)
                            .update("status", "approved")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "המגרש אושר ונוסף למפה", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "שגיאה בעדכון סטטוס הבקשה", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בהוספת המגרש", Toast.LENGTH_SHORT).show();
                });
    }
}
