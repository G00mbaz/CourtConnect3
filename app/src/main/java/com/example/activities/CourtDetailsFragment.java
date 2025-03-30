package com.example.activities;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.courtconnect3.R;

public class CourtDetailsFragment extends DialogFragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_HOURS = "hours";

    private String name;
    private String address;
    private String hours;

    public static CourtDetailsFragment newInstance(String name, String address, String hours) {
        CourtDetailsFragment fragment = new CourtDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_ADDRESS, address);
        args.putString(ARG_HOURS, hours);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            address = getArguments().getString(ARG_ADDRESS);
            hours = getArguments().getString(ARG_HOURS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_court_details, container, false);

        TextView nameTextView = view.findViewById(R.id.court_name);
        TextView addressTextView = view.findViewById(R.id.court_address);
        TextView hoursTextView = view.findViewById(R.id.court_hours);

        nameTextView.setText(name);
        addressTextView.setText("כתובת: " + address);
        hoursTextView.setText("שעות פעילות: " + hours);

        return view;
    }
}
