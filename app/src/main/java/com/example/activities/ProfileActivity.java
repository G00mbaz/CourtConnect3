package com.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.courtconnect3.R;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        Button BackToMapBtn;
        BackToMapBtn = findViewById(R.id.back_to_map_btn);
        BackToMapBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MapActivity.class));
            finish();
        });
        }
    }
