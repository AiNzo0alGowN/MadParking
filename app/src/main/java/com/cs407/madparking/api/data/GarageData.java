package com.cs407.madparking.api.data;

public class GarageData {
    private transient String name; // will be setting manully
    private String address;

    public GarageData(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public String getAddress() {
        return address;
    }
}
