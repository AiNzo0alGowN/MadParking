package com.cs407.madparking.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GarageData {
    @SerializedName("url")
    private String url;

    @SerializedName("name")
    private String name;

    @SerializedName("availability")
    private String availability;

    @SerializedName("addresses")
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

    public void setName(String name) {
        this.name = name;
    }

    public String getAddresses() {
        if (addresses == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String address : addresses) {
            sb.append(address).append("\n");
        }
        return sb.toString();
    }

    public GaragePrice getGaragePrice() {
        return new GaragePrice(rates);
    }
}
