package com.cs407.madparking;

import android.app.Application;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cs407.madparking.api.GarageData;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MadParking extends Application {

    private MadParkingDB db;
    private Location currentLocation;

    private final MutableLiveData<Map<String, GarageData>> parkingLots;

    public MadParking() {
        super();
        parkingLots = new MutableLiveData<>();

        // intialize database
        db = new MadParkingDB(this);

        // initialize current location to be the center of campus
        currentLocation = new Location(LocationManager.GPS_PROVIDER);
    }

    public LiveData<Map<String, GarageData>> getParkingLots() {
        return parkingLots;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public MadParkingDB getDb() {
        return db;
    }

    public void updateCurrentLocation(Location location) {
        currentLocation = location;
    }

    public void setParkingLots(Map<String, GarageData> parkingLots) {
        this.parkingLots.postValue(parkingLots);
    }

}
