package com.example.courtconnect3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RequestCourtActivity extends AppCompatActivity {

    EditText field_coordinates,field_name, field_hours, field_address;
    Button submit_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_court);

        EditText nameEditText = findViewById(R.id.court_name);
        EditText addressEditText = findViewById(R.id.court_address);
        EditText hoursEditText = findViewById(R.id.court_hours);
        EditText coordinatesEditText = findViewById(R.id.court_coordinates);
        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String hours = hoursEditText.getText().toString().trim();
            String coordinates = coordinatesEditText.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || hours.isEmpty() || coordinates.isEmpty()) {
                Toast.makeText(RequestCourtActivity.this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            // שמירת הבקשה (כדוגמה, שמירה ב-SharedPreferences)
            saveRequest(name, address, hours, coordinates);
            Toast.makeText(RequestCourtActivity.this, "הבקשה נשלחה בהצלחה", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveRequest(String name, String address, String hours, String coordinates) {
        // שמירה ל-SharedPreferences כדוגמה פשוטה
        getSharedPreferences("court_requests", MODE_PRIVATE)
                .edit()
                .putString(name, name + ";" + address + ";" + hours + ";" + coordinates)
                .apply();
    }
}
