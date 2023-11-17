package com.cs407.madparking.api;

import com.cs407.madparking.api.data.GarageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetParkingLotsResp {
    private final Map<String, GarageData> items;

    public GetParkingLotsResp(Map<String, GarageData> items) {
        this.items = items;
    }

    public List<GarageData> getItemsAsList() {
        List<GarageData> list = new ArrayList<>();
        for (Map.Entry<String, GarageData> entry : items.entrySet()) {
            entry.getValue().setName(entry.getKey());
            list.add(entry.getValue());
        }
        return list;
    }
}
