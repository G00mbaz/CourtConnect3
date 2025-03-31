package com.example.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.courtconnect3.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FirebaseFirestore db;
    private Button go_to_profile_btn;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        go_to_profile_btn = findViewById(R.id.go_to_profile_btn);
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FloatingActionButton fabOpenRequest = findViewById(R.id.fab_open_request);
        fabOpenRequest.setOnClickListener(view -> {
            Intent intent = new Intent(MapActivity.this, RequestCourtActivity.class);
            startActivity(intent);
        });

        go_to_profile_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation(); // הפעלת פונקציה להצגת המיקום הנוכחי של המשתמש
        loadApprovedCourts(); // טעינת המגרשים המאושרים

        // מאזין ללחיצה על מרקר
        mMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            String snippet = marker.getSnippet();
            String address = "";
            String lights = "";

            if (snippet != null) {
                String[] details = snippet.split("\n");
                if (details.length > 0) address = details[0].replace("כתובת: ", "");
                if (details.length > 1) lights = details[1].replace("שעות פעילות: ", "");
            }

            CourtDetailsFragment courtDetailsFragment = CourtDetailsFragment.newInstance(title, address, lights);
            courtDetailsFragment.show(getSupportFragmentManager(), "courtDetails");

            return true;
        });
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                mMap.addMarker(new MarkerOptions().position(userLocation).title("המיקום שלי"));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    private void loadApprovedCourts() {
        db.collection("courts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String address = document.getString("address");
                        String lights = document.getString("lights");
                        GeoPoint location = document.getGeoPoint("coordinates");

                        if (location != null) {
                            LatLng courtLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(courtLocation)
                                    .title(name)
                                    .snippet("כתובת: " + address + "\n" + "שעות פעילות: " + lights));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // הוספת הודעה למשתמש במקרה של שגיאה
                });
    }
}
