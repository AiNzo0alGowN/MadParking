package com.cs407.madparking.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cs407.madparking.api.data.GarageData;

import java.util.List;

public class StatisticsViewModel extends ViewModel {

    private MutableLiveData<List<GarageData>> parkingSpots = new MutableLiveData<>();

    public LiveData<List<GarageData>> getParkingSpots() {
        return parkingSpots;
    }

    public void loadParkingSpots() {
        // 使用 Retrofit 加载数据
        // 更新 parkingSpots
    }
}