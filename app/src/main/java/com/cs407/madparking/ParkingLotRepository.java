package com.cs407.madparking;

import com.cs407.madparking.api.GetParkingLotsResp;
import com.cs407.madparking.api.data.GarageData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class ParkingLotRepository {
    private ParkingLotApi parkingLotApi;

    public ParkingLotRepository() {
        parkingLotApi = ApiClient.getRetrofit().create(ParkingLotApi.class);
    }

    public void getParkingLots(Callback<GetParkingLotsResp> callback) {
        Call<GetParkingLotsResp> call = parkingLotApi.getParkingLots();
        call.enqueue(callback);
    }
}
