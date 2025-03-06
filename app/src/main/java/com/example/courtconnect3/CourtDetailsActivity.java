package com.example.courtconnect3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CourtDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_details);

        // קבלת הנתונים מה-Intent
        Intent intent = getIntent();
        String fieldName = intent.getStringExtra("fieldName");
        String fieldAddress = intent.getStringExtra("fieldAddress");
        String fieldHours = intent.getStringExtra("fieldHours");

        // מציאת הרכיבים ב-XML
        TextView nameTextView = findViewById(R.id.field_name);
        TextView addressTextView = findViewById(R.id.field_address);
        TextView hoursTextView = findViewById(R.id.field_hours);
        ImageView fieldImageView = findViewById(R.id.field_image);

        // הגדרת הנתונים לרכיבים
        nameTextView.setText(fieldName);
        addressTextView.setText(fieldAddress);
        hoursTextView.setText(fieldHours);


        // הכנה לאזור הצ'אט (נוסיף בהמשך)
    }
}
