package com.cs407.madparking;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ParkingLotApi {
    @GET("/parking-lots")
    Call<Map<String, Object>> getParkingLots();
}
