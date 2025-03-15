package com.example.courtconnect3;

import android.os.Bundle;
import android.view.View;
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

        db = FirebaseFirestore.getInstance();
        requestsLayout = findViewById(R.id.requests_layout);

        loadRequests();
    }

    private void loadRequests() {
        db.collection("courtRequests")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String requestId = document.getId();
                        String name = document.getString("name");
                        String address = document.getString("address");
                        String hours = document.getString("hours");
                        String coordinates = document.getString("coordinates");

                        LinearLayout requestContainer = new LinearLayout(this);
                        requestContainer.setOrientation(LinearLayout.VERTICAL);
                        requestContainer.setPadding(16, 16, 16, 16);

                        TextView requestView = new TextView(this);
                        requestView.setText("שם: " + name + "\nכתובת: " + address + "\nשעות: " + hours + "\nקואורדינטות: " + coordinates);
                        requestView.setPadding(16, 16, 16, 16);

                        Button approveButton = new Button(this);
                        approveButton.setText("אישור");
                        approveButton.setOnClickListener(view -> {
                            approveRequest(requestId, name, address, hours, coordinates);
                            requestsLayout.removeView(requestContainer);
                        });

                        requestContainer.addView(requestView);
                        requestContainer.addView(approveButton);
                        requestsLayout.addView(requestContainer);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בטעינת הבקשות", Toast.LENGTH_SHORT).show();
                });
    }

    private void approveRequest(String requestId, String name, String address, String hours, String coordinates) {
        Map<String, Object> field = new HashMap<>();
        field.put("name", name);
        field.put("address", address);
        field.put("hours", hours);
        field.put("coordinates", coordinates);

        db.collection("fields")
                .add(field)
                .addOnSuccessListener(documentReference -> {
                    db.collection("courtRequests").document(requestId)
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
