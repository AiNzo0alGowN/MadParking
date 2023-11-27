package com.cs407.madparking.ui.dashboard;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cs407.madparking.ParkingLotRepository;
import com.cs407.madparking.api.GetParkingLotsResp;
import com.cs407.madparking.api.data.GarageData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsViewModel extends ViewModel {
    private final ParkingLotRepository parkingLotRepository;

    public StatisticsViewModel(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public void updateData(){
        Log.d("StatisticsViewModel", "Updating data...");
        parkingLotRepository.getParkingLots(new Callback<GetParkingLotsResp>() {
            @Override
            public void onResponse(@NonNull Call<GetParkingLotsResp> call, @NonNull Response<GetParkingLotsResp> response) {
                if (response.isSuccessful()) {
                    if(response.body() == null){
                        Log.w("StatisticsViewModel", "Response body is null");
                        return;
                    }
                    StatisticsViewModel.this.loadParkingSpots(response.body().getItemsAsList());
                    Log.d("StatisticsViewModel", "Loaded data successfully, size: " + response.body().getItemsAsList().size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetParkingLotsResp> call, @NonNull Throwable t) {
                Log.w("StatisticsViewModel", "Failed to load data: ", t);
            }
        });
    }


    private MutableLiveData<List<GarageData>> parkingSpots = new MutableLiveData<>();

    public LiveData<List<GarageData>> getParkingSpots() {
        return parkingSpots;
    }

    public void loadParkingSpots(List<GarageData> parkingSpots) {
        Log.d("StatisticsViewModel", "Loading data...");
        this.parkingSpots.setValue(parkingSpots);
    }
}