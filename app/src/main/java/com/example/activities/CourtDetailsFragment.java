package com.example.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.courtconnect3.R;
import com.example.adapters.ChatAdapter;
import com.example.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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

    private TextView weatherTextView;

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

        TextView nameTextView = view.findViewById(R.id.court_name);
        TextView addressTextView = view.findViewById(R.id.court_address);
        TextView lightsTextView = view.findViewById(R.id.court_lights);
        weatherTextView = view.findViewById(R.id.weather_text);

        nameTextView.setText(courtName);
        addressTextView.setText("כתובת: " + address);
        lightsTextView.setText("תאורה: " + lights);

        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        chatInput = view.findViewById(R.id.chat_input);
        sendButton = view.findViewById(R.id.chat_send_button);
        addToFavoritesButton = view.findViewById(R.id.add_to_favorites_button);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

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
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        sendButton.setOnClickListener(v -> sendMessage());

        fetchWeatherForecast();

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

    private void fetchWeatherForecast() {
        String apiKey = "4e619e55f55aaa0c705b2b228c85384f\n"; // שים כאן את המפתח שלך
        String cityName = extractCityName(address);
        if (cityName == null) {
            weatherTextView.setText("שגיאה בזיהוי העיר למזג האוויר");
            return;
        }

        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&units=metric&appid=" + apiKey + "&lang=he";

        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                JSONObject response = new JSONObject(builder.toString());
                JSONArray list = response.getJSONArray("list");

                JSONObject eveningForecast = null;
                for (int i = 0; i < list.length(); i++) {
                    JSONObject forecast = list.getJSONObject(i);
                    String dateText = forecast.getString("dt_txt");
                    if (dateText.contains("18:00:00")) {
                        eveningForecast = forecast;
                        break;
                    }
                }

                if (eveningForecast != null) {
                    JSONObject main = eveningForecast.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    JSONArray weatherArray = eveningForecast.getJSONArray("weather");
                    String description = weatherArray.getJSONObject(0).getString("description");

                    String weatherText = "תחזית ערב: " + description + ", " + temp + "°C";

                    requireActivity().runOnUiThread(() -> weatherTextView.setText(weatherText));
                } else {
                    requireActivity().runOnUiThread(() -> weatherTextView.setText("אין תחזית זמינה לערב"));
                }

            } catch (Exception e) {
                Log.e("Weather", "Failed to fetch weather: " + e.getMessage());
                requireActivity().runOnUiThread(() -> weatherTextView.setText("שגיאה בטעינת מזג האוויר"));
            }
        }).start();
    }

    private String extractCityName(String fullAddress) {
        if (fullAddress.contains("Tel Aviv")) return "Tel Aviv";
        if (fullAddress.contains("Jerusalem")) return "Jerusalem";
        if (fullAddress.contains("Haifa")) return "Haifa";
        if (fullAddress.contains("Beersheba")) return "Beersheba";
        if (fullAddress.contains("Rishon LeZion")) return "Rishon LeZion";
        if (fullAddress.contains("Petah Tikva")) return "Petah Tikva";
        if (fullAddress.contains("Ramat Gan")) return "Ramat Gan";
        if (fullAddress.contains("Netanya")) return "Netanya";
        return null; // אם לא נמצא
    }
}
