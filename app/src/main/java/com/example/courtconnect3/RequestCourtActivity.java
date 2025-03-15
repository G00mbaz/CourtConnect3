package com.example.courtconnect3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class RequestCourtActivity extends AppCompatActivity {

    private EditText fieldCoordinates, fieldName, fieldHours, fieldAddress;
    private Button submitButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_court);

        fieldName = findViewById(R.id.court_name);
        fieldAddress = findViewById(R.id.court_address);
        fieldHours = findViewById(R.id.court_hours);
        fieldCoordinates = findViewById(R.id.court_coordinates);
        submitButton = findViewById(R.id.submit_button);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        submitButton.setOnClickListener(view -> {
            String name = fieldName.getText().toString().trim();
            String address = fieldAddress.getText().toString().trim();
            String hours = fieldHours.getText().toString().trim();
            String coordinates = fieldCoordinates.getText().toString().trim();
            String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "anonymous";

            if (name.isEmpty() || address.isEmpty() || hours.isEmpty() || coordinates.isEmpty()) {
                Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> courtRequest = new HashMap<>();
            courtRequest.put("name", name);
            courtRequest.put("address", address);
            courtRequest.put("hours", hours);
            courtRequest.put("coordinates", coordinates);
            courtRequest.put("userId", userId);
            courtRequest.put("status", "pending");

            firestore.collection("courtRequests").add(courtRequest)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "הבקשה נשלחה בהצלחה!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "שגיאה בשליחת הבקשה: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
