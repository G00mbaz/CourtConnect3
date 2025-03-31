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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.courtconnect3.R;
import com.example.adapters.ChatAdapter;
import com.example.models.ChatMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        chatDatabase = FirebaseDatabase.getInstance().getReference("chats").child(courtName);

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String messageText = chatInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            ChatMessage message = new ChatMessage("User", messageText, System.currentTimeMillis(), "");
            chatDatabase.push().setValue(message);
            chatInput.setText("");
        }
    }
}
