package com.example.courtconnect3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText registerfullname, registerEmail, registerPassword;
    Button registerbtn, backToLoginBtn; // הוסף משתנה לכפתור "חזרה לכניסה"
    TextView registerErrorMessage;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // חיבור אלמנטים מה-XML
        registerfullname = findViewById(R.id.registerfullname);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerbtn = findViewById(R.id.registerbtn);
        backToLoginBtn = findViewById(R.id.backToLoginBtn); // התחבר לכפתור "חזרה לכניסה"

        // אתחול Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // מאזין ללחיצה על כפתור ההרשמה
        registerbtn.setOnClickListener(v -> {
            String fullName = registerfullname.getText().toString().trim();
            String email = registerEmail.getText().toString().trim();
            String password = registerPassword.getText().toString().trim();

            // בדיקת שדות ריקים
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            // הרשמת המשתמש ב-Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // קבלת UID של המשתמש החדש
                            String userId = auth.getCurrentUser().getUid();

                            // יצירת מפת נתונים עבור Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("fullName", fullName);
                            userMap.put("email", email);

                            // שמירת הנתונים ב-Firestore תחת אוסף "users"
                            firestore.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(RegisterActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(RegisterActivity.this, "שגיאה בשמירת הנתונים: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(RegisterActivity.this, "הרשמה נכשלה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // מאזין ללחיצה על כפתור "חזרה לכניסה"
        backToLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // מסיים את הפעילות הנוכחית
        });
    }
}
