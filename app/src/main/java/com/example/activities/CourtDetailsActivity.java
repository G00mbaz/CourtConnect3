package com.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.courtconnect3.R;

public class CourtDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_details);

        // קבלת הנתונים מה-Intent
        Intent intent = getIntent();
        String fieldName = intent.getStringExtra("courtName");
        String fieldAddress = intent.getStringExtra("courtAddress");
        String fieldHours = intent.getStringExtra("courtHours");

        // מציאת הרכיבים ב-XML
        TextView nameTextView = findViewById(R.id.court_name);
        TextView addressTextView = findViewById(R.id.court_address);
        TextView hoursTextView = findViewById(R.id.court_lights);
        ImageView fieldImageView = findViewById(R.id.court_image);

        // הגדרת הנתונים לרכיבים
        nameTextView.setText(fieldName);
        addressTextView.setText(fieldAddress);
        hoursTextView.setText(fieldHours);


        // הכנה לאזור הצ'אט (נוסיף בהמשך)
    }
}
