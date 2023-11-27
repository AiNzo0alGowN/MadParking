package com.cs407.madparking;

import com.cs407.madparking.api.GetParkingLotsResp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ParkingLotApi {
    @GET("/parking-lots")
    Call<GetParkingLotsResp> getParkingLots();
}
