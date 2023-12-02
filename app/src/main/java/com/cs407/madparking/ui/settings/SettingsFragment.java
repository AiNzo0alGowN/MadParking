package com.cs407.madparking.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs407.madparking.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Dark mode switch
        Switch switchDarkMode = binding.switchDarkMode;
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
        });


        // Saved address
        String savedAddress = sharedPreferences.getString("saved_address", "");
        binding.buttonAddress.setText(savedAddress);

        binding.buttonAddress.setOnClickListener(v -> showEditAddressDialog());

        return root;
    }

    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Address");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(sharedPreferences.getString("saved_address", "")); // Prefill with saved address if available

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String address = input.getText().toString();
            saveAddressToLocalStorage(address);
            binding.buttonAddress.setText(address);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveAddressToLocalStorage(String address) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_address", address);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
