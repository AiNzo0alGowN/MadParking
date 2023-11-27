package com.cs407.madparking;

import java.util.HashMap;
import java.util.Map;

public class GlobalData {
    private static GlobalData instance;
    private Map<String, Object> parkingLotsData;

    private GlobalData() {
        parkingLotsData = new HashMap<>();
    }

    public static synchronized GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }

    public Map<String, Object> getParkingLotsData() {
        return parkingLotsData;
    }

    public void setParkingLotsData(Map<String, Object> data) {
        this.parkingLotsData = data;
    }
}

