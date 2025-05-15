package com.example.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adapters.FavoriteCourtsAdapter;
import com.example.courtconnect3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView fullNameText;
    private RecyclerView favoritesRecyclerView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Button logoutButton, backButton;
    private List<String> favoriteCourts = new ArrayList<>();
    private FavoriteCourtsAdapter adapter;
    private Uri imageUri;
    private String uid;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadImageToFirebase();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        fullNameText = findViewById(R.id.full_name_text);
        favoritesRecyclerView = findViewById(R.id.favorite_courts_list);
        logoutButton = findViewById(R.id.button_logout);
        backButton = findViewById(R.id.buttonBackToMap);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // קבלת UID של המשתמש (או המשתמש שאליו עברנו דרך הצ'אט)
        uid = getIntent().getStringExtra("userId");
        if (uid == null) {
            uid = auth.getCurrentUser().getUid();
        }

        adapter = new FavoriteCourtsAdapter(favoriteCourts, getSupportFragmentManager());
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(adapter);

        loadUserProfile();

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());

        // בחר תמונה חדשה בלחיצה על התמונה
        profileImage.setOnClickListener(v -> openImagePicker());
    }

    private void loadUserProfile() {
        firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        fullNameText.setText(fullName != null ? fullName : "שם לא ידוע");

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this).load(profileImageUrl).into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.default_profile);
                        }

                        List<String> courts = (List<String>) documentSnapshot.get("favoriteCourts");
                        if (courts != null) {
                            favoriteCourts.clear();
                            favoriteCourts.addAll(courts);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בטעינת הפרופיל", Toast.LENGTH_SHORT).show());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToFirebase() {
        if (imageUri != null && uid != null) {
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("profile_images/" + uid + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        firestore.collection("users").document(uid)
                                .update("profileImageUrl", imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Glide.with(ProfileActivity.this).load(imageUrl).into(profileImage);
                                    Toast.makeText(this, "תמונת הפרופיל עודכנה", Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בהעלאת התמונה", Toast.LENGTH_SHORT).show());
        }
    }
}
