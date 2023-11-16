package com.cs407.retrofit_test;

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

    public void getParkingLots(MainActivity mainActivity) {
        Call<Map<String, Object>> call = parkingLotApi.getParkingLots();
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    mainActivity.updateTextViewWithParkingData(response.body());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("ParkingLotRepository", "API Request Failed", t);
            }
        });
    }
}
