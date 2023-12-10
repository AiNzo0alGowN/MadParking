package com.cs407.madparking.ui.map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cs407.madparking.R;

import static android.content.Context.MODE_PRIVATE;

public class MapSetting extends Fragment {

    private Switch switchTraffic, switchPosition;

    public MapSetting() {

    }

    public static MapSetting newInstance() {
        return new MapSetting();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mapping_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the switch
        switchTraffic = view.findViewById(R.id.switchTraffic);
        switchPosition = view.findViewById(R.id.switchPosition);

        // Load saved preferences
        SharedPreferences mapSetting = getActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isTrafficEnabled = mapSetting.getBoolean("TrafficEnabled", false);
        boolean isPositionEnabled = mapSetting.getBoolean("PositionEnabled", false);
        switchTraffic.setChecked(isTrafficEnabled);
        switchPosition.setChecked(isPositionEnabled);

        // Set up listeners for the switchs
        switchTraffic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the switch state in shared preferences
            SharedPreferences.Editor editor = mapSetting.edit();
            editor.putBoolean("TrafficEnabled", isChecked);
            editor.apply();

            // Provide feedback to the user
            Toast.makeText(getContext(), "Traffic setting updated", Toast.LENGTH_SHORT).show();
        });

        switchPosition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = mapSetting.edit();
            editor.putBoolean("PositionEnabled", isChecked); // Save the new setting
            editor.apply();
            Toast.makeText(getContext(), "Position setting updated", Toast.LENGTH_SHORT).show();
        });

        // Set up a listener for the confirm button
        Button confirmButton = view.findViewById(R.id.Confirm_button);
        confirmButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }
}
