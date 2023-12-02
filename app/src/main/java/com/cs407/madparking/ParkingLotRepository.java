package com.cs407.madparking;

import android.util.Log;

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
}
