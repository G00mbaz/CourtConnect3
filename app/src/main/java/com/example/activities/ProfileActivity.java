package com.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.adapters.FavoriteCourtsAdapter;
import com.example.courtconnect3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button logoutButton = findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        profileImage = findViewById(R.id.profile_image);
        fullNameText = findViewById(R.id.full_name_text);
        favoritesRecyclerView = findViewById(R.id.favorite_courts_list);
        Button backButton = findViewById(R.id.buttonBackToMap);
        backButton.setOnClickListener(v -> {
            finish();
        });

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String uid = getIntent().getStringExtra("userId");
        if (uid == null) {
            uid = auth.getCurrentUser().getUid();
        }

        adapter = new FavoriteCourtsAdapter(favoriteCourts, getSupportFragmentManager());
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(adapter);

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
                });
    }

}
