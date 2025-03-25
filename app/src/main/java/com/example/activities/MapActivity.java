package com.example.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.courtconnect3.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // טוען את המפה
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // קישור לכפתור הצף לפתיחת בקשה
        FloatingActionButton fabOpenRequest = findViewById(R.id.fab_open_request);

        // פעולה בלחיצה על הכפתור
        fabOpenRequest.setOnClickListener(view -> {
            Intent intent = new Intent(MapActivity.this, RequestCourtActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // בקשת הרשאה למיקום בזמן ריצה
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }



        // הוספת המיקום של המגרש
        LatLng schoolField = new LatLng(31.25255437987353, 34.801378713607086); // קורדינטות של בית הספר
        MarkerOptions schoolMarker = new MarkerOptions()
                .position(schoolField)
                .title("מגרש בבית הספר")
                .snippet("שעות פעילות: 08:00-22:00");
        mMap.addMarker(schoolMarker);

        // מאזין ללחיצה על סמן
        mMap.setOnMarkerClickListener(marker -> {
            openFieldDetails(marker);
            return true;
        });
    }

    // פתיחת מסך פרטי המגרש
    private void openFieldDetails(Marker marker) {
        Intent intent = new Intent(MapActivity.this, CourtDetailsActivity.class);
        intent.putExtra("field_name", marker.getTitle());
        intent.putExtra("field_address", "כתובת: רחוב הדוגמה 1");
        intent.putExtra("field_hours", marker.getSnippet());
        startActivity(intent);
    }
}
