package com.cs407.retrofit_test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingLotRepository {
    private ParkingLotApi parkingLotApi;

    public ParkingLotRepository() {
        parkingLotApi = ApiClient.getRetrofit().create(ParkingLotApi.class);
    }

    public void getParkingLots(Callback<ParkingLotList> callback) {
        Call<ParkingLotList> call = parkingLotApi.getParkingLots();
        call.enqueue(callback);
    }

    public void getParkingLot(int id, Callback<ParkingLot> callback) {
        Call<ParkingLot> call = parkingLotApi.getParkingLot(id);
        call.enqueue(callback);
    }
}
