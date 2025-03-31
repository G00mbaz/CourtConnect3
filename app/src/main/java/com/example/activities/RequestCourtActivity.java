package com.example.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.models.CourtRequests;
import com.example.courtconnect3.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.Locale;

public class RequestCourtActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Spinner lightsSpinner;
    private LatLng selectedLatLng = null;
    private String selectedAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_court);

        nameEditText = findViewById(R.id.editTextCourtName);
        lightsSpinner = findViewById(R.id.spinnerLights);
        Button submitButton = findViewById(R.id.buttonSubmitCourtRequest);

        // אתחול Places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAek5fmyIgki42Wh2Vo5OMLrYZ3TozFAWk", Locale.getDefault());
        }

        // הגדרת אוטוקומפליט של כתובת
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    selectedLatLng = place.getLatLng();
                    selectedAddress = place.getAddress();
                    Log.i("PLACE", "Selected: " + selectedAddress + " at " + selectedLatLng);
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e("PLACE", "Error: " + status);
                    Toast.makeText(RequestCourtActivity.this,
                            "שגיאה בבחירת כתובת: " + status.getStatusMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCourtRequest();
            }
        });
    }

    private void submitCourtRequest() {
        String name = nameEditText.getText().toString().trim();
        String lights = lightsSpinner.getSelectedItem().toString();

        if (name.isEmpty() || selectedLatLng == null || selectedAddress == null) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint geoPoint = new GeoPoint(selectedLatLng.latitude, selectedLatLng.longitude);
        CourtRequests courtRequest = new CourtRequests(name, selectedAddress, lights, geoPoint);

        FirebaseFirestore.getInstance()
                .collection("courtRequests")
                .add(courtRequest)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "הבקשה נשלחה!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בשליחה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
