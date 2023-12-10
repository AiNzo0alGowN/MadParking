package com.cs407.madparking;

import android.util.Log;

import com.cs407.madparking.api.GarageData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingLotRepository {
    private ParkingLotApi parkingLotApi;

    public ParkingLotRepository() {
        parkingLotApi = ApiClient.getRetrofit().create(ParkingLotApi.class);
    }

    public void getParkingLots(Callback<Map<String, Object>> callback) {
        Call<Map<String, Object>> call = parkingLotApi.getParkingLots();
        call.enqueue(callback);
    }

    public void getParkingLotsAdv(Callback<Map<String, GarageData>> callback) {
        Call<Map<String, GarageData>> call = parkingLotApi.getParkingLotsAdv();
        call.enqueue(callback);
    }
}
