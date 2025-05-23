package com.example.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.courtconnect3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String ADMIN_EMAIL = "si.yacobi@gmail.com";
    private static final String ADMIN_PASSWORD = "Simo@2007";

    EditText loginemail;
    EditText loginpassword;
    Button loginbtn;
    TextView tv_login_register;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // חיבור רכיבי ה-XML לקוד[
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Avigdor is Digger");
        loginemail = findViewById(R.id.loginemail);
        loginpassword = findViewById(R.id.loginpassword);
        loginbtn = findViewById(R.id.loginbtn);
        tv_login_register = findViewById(R.id.tv_login_register);

        // אתחול Firebase
        auth = FirebaseAuth.getInstance();

        // מאזין ללחיצה על כפתור ההתחברות
        loginbtn.setOnClickListener(v -> {
            String email = loginemail.getText().toString().trim();
            String password = loginpassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            // בדיקת משתמש מנהל
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                // התחברות מנהל - מעבר למסך בקשות
                Intent adminIntent = new Intent(MainActivity.this, AdminConfirmActivity.class);
                startActivity(adminIntent);
                finish();
                return;
            }

            // התחברות משתמש רגיל דרך Firebase
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                            Intent userIntent = new Intent(MainActivity.this, MapActivity.class);
                            startActivity(userIntent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "שגיאה בהתחברות: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // מאזין ללחיצה על כפתור הרשמה
        tv_login_register.setOnClickListener(v -> {
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });
    }
}
//simonyacobi@gmail.com
//sisma123