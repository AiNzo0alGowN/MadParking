package com.cs407.madparking.ui.statistics;

import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cs407.madparking.MadParking;
import com.cs407.madparking.ParkingLotRepository;
import com.cs407.madparking.api.GarageData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsViewModel extends ViewModel {

    private final Map<String, List<android.location.Address>> locationCache;

    private final MadParking application;
    private final ParkingLotRepository parkingLotRepository;
    private final MutableLiveData<GarageData> nearestGarage;
    private final ExecutorService executorService;


    public StatisticsViewModel(MadParking application, ParkingLotRepository parkingLotRepository) {
        this.application = application;
        this.locationCache = new HashMap<>();
        this.nearestGarage = new MutableLiveData<>();
        this.parkingLotRepository = parkingLotRepository;
        this.executorService = Executors.newSingleThreadExecutor();

        // add observer to current location
        application.getParkingLots().observeForever(parkingLots -> {
            if (parkingLots == null) {
                Log.w("StatisticsViewModel", "Parking lots is null");
                return;
            }
            Log.d("StatisticsViewModel", "Parking lots changed, size: " + parkingLots.size());
            UpdateNearestGarage(parkingLots);
        });
    }

    public void updateData(SwipeRefreshLayout layout) {
        Log.d("StatisticsViewModel", "Updating data...");
        parkingLotRepository.getParkingLotsAdv(new Callback<Map<String, GarageData>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, GarageData>> call, @NonNull Response<Map<String, GarageData>> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Log.w("StatisticsViewModel", "Response body is null");
                        return;
                    }

                    StatisticsViewModel.this.loadParkingSpots(response.body());
                    Log.d("StatisticsViewModel", "Loaded data successfully, size: " + response.body().size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, GarageData>> call, @NonNull Throwable t) {
                Log.w("StatisticsViewModel", "Failed to load data: ", t);
                layout.setRefreshing(false);

                // print toast
                Toast.makeText(application, "Failed to load data, pull to refresh again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected void UpdateNearestGarage(Map<String, GarageData> parkingLots) {
        executorService.execute(() -> {
            Log.d("StatisticsViewModel", "Updating nearest garage...");
            GarageData closestAddress = null;
            float shortestDistance = Float.MAX_VALUE;
            Geocoder geocoder = new Geocoder(application.getApplicationContext(), Locale.getDefault());

            try {
                // do name fixup
                for (String name : parkingLots.keySet()) {
                    if(parkingLots.get(name).getName() == null || parkingLots.get(name).getName().equals("")) {
                        parkingLots.get(name).setName(name);
                    }
                }

                // calculate nearest garage by distance to current location
                for (GarageData garage : parkingLots.values()) {
                    // skip if address is not avaliable
                    if (garage.getAddresses() == null || garage.getAddresses().equals("")) {
                        Log.d("StatisticsViewModel", "Skipping garage: " + garage.getName());
                        continue;
                    }

                    if (locationCache.containsKey(garage.getAddresses())) {
                        Log.d("StatisticsViewModel", "Using cached location for: " + garage.getName());
                    } else {
                        Log.d("StatisticsViewModel", "Geocoding address: " + garage.getAddresses());
                        List<android.location.Address> geoResults = geocoder.getFromLocationName(garage.getAddresses(), 1);
                        assert geoResults != null;
                        locationCache.put(garage.getAddresses(), geoResults);
                    }
                    if (Objects.requireNonNull(locationCache.get(garage.getAddresses())).size() > 0) {
                        List<android.location.Address> geoResults = locationCache.get(garage.getAddresses());
                        assert geoResults != null;
                        Log.d("StatisticsViewModel", "Nearest garage: " + garage.getName() + " " + geoResults.get(0).getLatitude() + " " + geoResults.get(0).getLongitude());
                        android.location.Address addressLocation = geoResults.get(0);
                        float[] results = new float[1];
                        Location.distanceBetween(application.getCurrentLocation().getLatitude(), application.getCurrentLocation().getLongitude(),
                                addressLocation.getLatitude(), addressLocation.getLongitude(), results);
                        float distance = results[0];
                        if (distance < shortestDistance) {
                            shortestDistance = distance;
                            closestAddress = garage;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // update nearest garage on the main thread
            if (closestAddress != null) {
                Log.d("StatisticsViewModel", "Nearest garage: " + closestAddress.getName());
                nearestGarage.postValue(closestAddress);
            }
        });
    }


    public LiveData<GarageData> getNearestGarage() {
        return nearestGarage;
    }

    public LiveData<Map<String, GarageData>> getParkingSpots() {
        return application.getParkingLots();
    }

    public void loadParkingSpots(Map<String, GarageData> parkingSpots) {
        Log.d("StatisticsViewModel", "Loading data...");

        Log.d("StatisticsViewModel", parkingSpots.keySet().toString());
        application.setParkingLots(parkingSpots);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}