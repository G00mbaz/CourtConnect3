package com.example.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.courtconnect3.R;
import com.example.adapters.ChatAdapter;
import com.example.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CourtDetailsFragment extends DialogFragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_LIGHTS = "lights";

    private String courtName;
    private String address;
    private String lights;

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private DatabaseReference chatDatabase;
    private EditText chatInput;
    private ImageButton sendButton;
    private Button addToFavoritesButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public static CourtDetailsFragment newInstance(String name, String address, String lights) {
        CourtDetailsFragment fragment = new CourtDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_ADDRESS, address);
        args.putString(ARG_LIGHTS, lights);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courtName = getArguments().getString(ARG_NAME);
            address = getArguments().getString(ARG_ADDRESS);
            lights = getArguments().getString(ARG_LIGHTS);
        }

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_court_details, container, false);

        // הצגת פרטי המגרש
        TextView nameTextView = view.findViewById(R.id.court_name);
        TextView addressTextView = view.findViewById(R.id.court_address);
        TextView lightsTextView = view.findViewById(R.id.court_lights);

        nameTextView.setText(courtName);
        addressTextView.setText("כתובת: " + address);
        lightsTextView.setText("שעות פעילות: " + lights);

        // הגדרת רכיבי הצ'אט
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        chatInput = view.findViewById(R.id.chat_input);
        sendButton = view.findViewById(R.id.chat_send_button);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);
        addToFavoritesButton = view.findViewById(R.id.add_to_favorites_button);

        chatDatabase = FirebaseDatabase.getInstance("https://courtconnect3-b362b-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("chats").child(courtName);

        addToFavoritesButton.setOnClickListener(v -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) return;

            String uid = currentUser.getUid();
            firestore.collection("users").document(uid)
                    .update("favoriteCourts", FieldValue.arrayUnion(courtName))
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "המגרש נוסף למועדפים!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "שגיאה בהוספה למועדפים", Toast.LENGTH_SHORT).show());
        });

        // האזנה להודעות בזמן אמת
        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        chatMessages.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // טיפול בשגיאה אם נרצה
            }
        });

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String messageText = chatInput.getText().toString().trim();
        if (messageText.isEmpty()) return;

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String fullName = documentSnapshot.exists() ? documentSnapshot.getString("fullName") : "User";
                    String profileImageUrl = documentSnapshot.exists() ? documentSnapshot.getString("profileImageUrl") : "";

                    ChatMessage message = new ChatMessage(uid, fullName, messageText, System.currentTimeMillis(), profileImageUrl);
                    chatDatabase.push().setValue(message);
                    chatInput.setText("");
                });
    }



}
