package com.cs407.madparking.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GarageData {
    @SerializedName("url")
    private String url;

    @SerializedName("name")
    private String name;

    @SerializedName("availability")
    private String availability;

    @SerializedName("address")
    private List<String> addresses;

    @SerializedName("hours")
    private String hours;

    @SerializedName("locations_nearby")
    private String locations_nearby;

    @SerializedName("rates")
    private String rates;

    public String getName() {
        return name;
    }

    public GaragePrice getGaragePrice() {
        return new GaragePrice(rates);
    }
}
