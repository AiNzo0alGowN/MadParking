package com.cs407.retrofit_test;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ParkingLotApi {
    @GET("/parking-lots")
    Call<ParkingLotList> getParkingLots();

    @GET("/parking-lots/{id}")
    Call<ParkingLot> getParkingLot(@Path("id") int id);
}
