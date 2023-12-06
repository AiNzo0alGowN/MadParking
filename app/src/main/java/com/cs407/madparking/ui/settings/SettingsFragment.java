package com.cs407.madparking.ui.settings;

import static android.app.appsearch.AppSearchResult.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs407.madparking.R;
import com.cs407.madparking.databinding.FragmentSettingsBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private Spinner distanceSpinner;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Places.isInitialized()) {
            Places.initialize(requireContext().getApplicationContext(), "AIzaSyCzcC0Gh80sKlmbJgeIV9YkqkMRsWV4uPM"); // Replace with your API key
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Set up the dark mode switch
        Switch switchDarkMode = binding.switchDarkMode;
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
        });

        // Load saved address and set click listener
        String savedAddress = sharedPreferences.getString("saved_address", "");
        binding.buttonAddress.setText(savedAddress.isEmpty() ? "Enter address" : savedAddress);
        binding.buttonAddress.setOnClickListener(v -> launchAutocompleteActivity());

        // Range Setting
        distanceSpinner = binding.distanceSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.distance_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter);

        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveDistanceOption(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadDistanceOption();
        return root;
    }

    private void launchAutocompleteActivity() {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .build(requireContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode != RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            String address = place.getName();

            LatLng latLng = place.getLatLng();
            binding.buttonAddress.setText(address);

            saveAddressToLocalStorage(address, latLng);
        }
    }

    private void saveAddressToLocalStorage(String address, LatLng latLng) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("saved_address", address);
        editor.putFloat("latitude", (float)latLng.latitude);
        editor.putFloat("longitude", (float)latLng.longitude);
        editor.apply();
    }

    private void saveDistanceOption(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selected_distance_option", position);
        editor.apply();
    }

    private void loadDistanceOption() {
        Integer selectedPosition = sharedPreferences.getInt("selected_distance_option", 0);
        distanceSpinner.setSelection(selectedPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
