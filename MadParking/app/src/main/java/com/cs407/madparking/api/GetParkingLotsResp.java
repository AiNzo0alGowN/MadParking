package com.cs407.madparking.api;

import com.cs407.madparking.api.data.GarageData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetParkingLotsResp {
    private Map<String, GarageData> parkingLots;

    // 构造函数，初始化 Map
    public GetParkingLotsResp() {
        parkingLots = new HashMap<>();
    }

    // 获取停车场信息的方法
    public Map<String, GarageData> getParkingLots() {
        return parkingLots;
    }

    // 设置停车场信息的方法
    public void setParkingLots(Map<String, GarageData> parkingLots) {
        this.parkingLots = parkingLots;
    }

    public List<GarageData> getItemsAsList() {
        // remove all GarageData that don't have a name
        parkingLots.values().removeIf(garageData -> garageData.getName() == null);
        return parkingLots == null ? null : new ArrayList<>(parkingLots.values());
    }
}
