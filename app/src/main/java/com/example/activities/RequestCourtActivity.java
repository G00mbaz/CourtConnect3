package com.example.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.courtconnect3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class RequestCourtActivity extends AppCompatActivity {

    private EditText courtCoordinates, courtName, courtAddress, courtLights;
    private Button submitButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Button returntomap_btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_court);

        courtName = findViewById(R.id.court_name);
        courtAddress = findViewById(R.id.court_address);
        courtCoordinates = findViewById(R.id.court_coordinates);
        courtLights=findViewById(R.id.court_lights);
        submitButton = findViewById(R.id.submit_button);
        returntomap_btn1 = findViewById(R.id.returntomap_btn1);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        returntomap_btn1.setOnClickListener(view -> {
            finish();
        });

        submitButton.setOnClickListener(view -> {
            String name = courtName.getText().toString().trim();
            String address = courtAddress.getText().toString().trim();
            String lights = courtLights.getText().toString().trim();
            String coordinates = courtCoordinates.getText().toString().trim();
            String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "anonymous";

            if (name.isEmpty() || address.isEmpty() || coordinates.isEmpty()) {
                Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String[] latLng = coordinates.split(",");
                double latitude = Double.parseDouble(latLng[0].trim());
                double longitude = Double.parseDouble(latLng[1].trim());
                GeoPoint location = new GeoPoint(latitude, longitude);

                Map<String, Object> courtRequest = new HashMap<>();
                courtRequest.put("name", name);
                courtRequest.put("address", address);
                courtRequest.put("lights", lights);
                courtRequest.put("location", location);
                courtRequest.put("userId", userId);
                courtRequest.put("status", "pending");

                firestore.collection("courtRequests").add(courtRequest)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "הבקשה נשלחה בהצלחה!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "שגיאה בשליחת הבקשה: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                Toast.makeText(this, "שגיאה בקואורדינטות. ודא שהפורמט הוא 'latitude,longitude'", Toast.LENGTH_LONG).show();
            }
        });
    }
}
