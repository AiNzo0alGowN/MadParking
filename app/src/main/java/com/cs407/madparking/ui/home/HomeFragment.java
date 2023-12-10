package com.cs407.madparking.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.madparking.ParkingLotRepository;
import com.cs407.madparking.databinding.FragmentHomeBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private ParkingLotRepository parkingLotRepository; // Ensure this is initialized

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.titleHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Initialize parkingLotRepository here (replace with your initialization logic)
        parkingLotRepository = new ParkingLotRepository();

        loadData();
        return root;
    }

    private void loadData() {
        if (parkingLotRepository != null) {
            parkingLotRepository.getParkingLots(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        LatLng storedAddress = getStoredAddress();
                        Map<String, Object> parkingLotsData = response.body();
                        int totalRequests = parkingLotsData.size();
                        int[] completedRequests = {0};
                        int range = loadDistanceOption();
                        boolean ev_support = getEvSupportStatus();

                        // Use a temporary list or map to store filtered data
                        Map<String, Object> filteredParkingLots = new HashMap<>();

                        for (Map.Entry<String, Object> entry : parkingLotsData.entrySet()) {
                            String key = entry.getKey();
                            Map<?, ?> parkingLotDetails = (Map<?, ?>) entry.getValue();
                            if (parkingLotDetails.get("ev_charging") != null || !ev_support) {
                                Log.d("HF", parkingLotDetails.get("addresses").toString());
                                String address = parkingLotDetails.get("addresses").toString().replace("[", "").replace("]", "");
                                getLatLng(address, latLng -> {
                                    completedRequests[0]++;
                                    if (latLng != null && SphericalUtil.computeDistanceBetween(latLng, storedAddress) <= range) {
                                        filteredParkingLots.put(key, parkingLotDetails);
                                    }
                                    // Check if all requests are completed
                                    if (completedRequests[0] == totalRequests) {
                                        // Update adapter on the main thread
                                        handler.post(() -> {
                                            adapter = new MyListAdapter(filteredParkingLots);
                                            recyclerView.setAdapter(adapter);
                                        });
                                    }
                                });
                            } else {
                                completedRequests[0]++;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    // Handle failure
                }
            });
        } else {
            Log.d("HomeFragment", "ERR");
        }
    }

    private LatLng getStoredAddress() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        float lat = sharedPreferences.getFloat("latitude", (float) 43.0849639);
        float lng = sharedPreferences.getFloat("longitude", (float) -89.4889926);

        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode_status", false);
        int mode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
        return new LatLng(lat, lng);
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private void getLatLng(String address, Consumer<LatLng> callback) {
        executorService.execute(() -> {
            try {
                String apiKey = "AIzaSyCzcC0Gh80sKlmbJgeIV9YkqkMRsWV4uPM";
                String requestUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                        Uri.encode(address) + "&key=" + apiKey;

                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    json.append(line).append('\n');
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(json.toString());
                JSONObject location = jsonObject.getJSONArray("results").getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");

                LatLng latLng = new LatLng(latitude, longitude);

                // Switch back to main thread to return the result
                handler.post(() -> callback.accept(latLng));

            } catch (Exception e) {
                Log.e("ERR", e.toString());
                handler.post(() -> callback.accept(null));
            }
        });
    }

    private int loadDistanceOption() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        int selectedPosition = sharedPreferences.getInt("selected_distance_option", 3);
        if (selectedPosition == 0) {
            return 100;
        } else if (selectedPosition == 1) {
            return 200;
        } else if (selectedPosition == 2) {
            return 500;
        } else {
            return 9999999;
        }
    }

    private boolean getEvSupportStatus() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isEvSupport = sharedPreferences.getBoolean("ev_support_status", false);
        return isEvSupport;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
