package com.cs407.madparking;

import com.cs407.madparking.api.GarageData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ParkingLotApi {
    @GET("/parking-lots")
    Call<Map<String, Object>> getParkingLots();

    @GET("/parking-lots")
    Call<Map<String, GarageData>> getParkingLotsAdv();
}
