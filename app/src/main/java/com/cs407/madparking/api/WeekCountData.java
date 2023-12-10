package com.cs407.madparking.api;

public class WeekCountData {
    private final double price;

    private final String garageName;

    public WeekCountData(String garageName, double price) {
        this.price = price;
        this.garageName = garageName;
    }

    public double getPrice() {
        return price;
    }

    public String getGarageName() {
        return garageName;
    }
}
