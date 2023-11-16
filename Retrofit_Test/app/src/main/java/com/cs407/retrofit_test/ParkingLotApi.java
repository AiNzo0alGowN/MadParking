package com.cs407.retrofit_test;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.Map;

public interface ParkingLotApi {
    @GET("/parking-lots")
    Call<Map<String, Object>> getParkingLots();
}
