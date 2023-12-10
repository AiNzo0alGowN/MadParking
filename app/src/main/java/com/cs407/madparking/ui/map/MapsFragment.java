package com.cs407.madparking.ui.map;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cs407.madparking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ListView listViewParking;
    private ArrayList<String> parkingLotList;
    private List<LatLng> listLatLng;

    private Marker userPositionMarker;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Map<Integer, Marker> markerMap = new HashMap<>();

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    addUserPositionMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        };
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    //used to add the marker from the given address
    private class GeocodeAsyncTask extends AsyncTask<String, Void, LatLng> {
        private GoogleMap taskMap;
        private FetchAddressAsyncTask.ParkingLotInfo parkingLotInfo;

        public GeocodeAsyncTask(GoogleMap map, FetchAddressAsyncTask.ParkingLotInfo parkingLotInfo) {
            this.taskMap = map;
            this.parkingLotInfo = parkingLotInfo;
        }

        public GeocodeAsyncTask(GoogleMap map) {
            this.taskMap = map;
        }
        @Override
        protected LatLng doInBackground(String... addresses) {
            try {
                String address = addresses[0];
                String apiKey = "AIzaSyCzcC0Gh80sKlmbJgeIV9YkqkMRsWV4uPM";
                String requestUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                        Uri.encode(address) + "&key=" + apiKey;

                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    json.append(line).append('\n');
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(json.toString());
                JSONObject location = jsonObject.getJSONArray("results").getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");

                return new LatLng(latitude, longitude);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if (latLng != null) {
                // Add marker with lot name and availability
                listLatLng.add(latLng);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(parkingLotInfo.name)
                        .snippet("Availability: " + parkingLotInfo.availability));
                // Assuming parkingLotInfo has a unique identifier, like an index or ID
                markerMap.put(parkingLotInfo.id, marker);
            }
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewParking = view.findViewById(R.id.listViewParking);
        parkingLotList = new ArrayList<>();

        listLatLng = new ArrayList<>();




        // Set up a click listener for the ListView items
        listViewParking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check if the listLatLng has an item at the clicked position
                if (position < listLatLng.size()) {
                    LatLng selectedLocation = listLatLng.get(position);
                    addMarkerOnMap(selectedLocation);

                }
                Marker marker = markerMap.get(position);
                if (marker != null) {
                    marker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                }
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void addMarkerOnMap(LatLng location) {
        if (mMap != null) {
            //mMap.addMarker(new MarkerOptions().position(location).title("Selected Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
    }

    private void readApi(){
        new FetchAddressAsyncTask().execute();
    }


    private class FetchAddressAsyncTask extends AsyncTask<Void, Void, List<FetchAddressAsyncTask.ParkingLotInfo>> {
        class ParkingLotInfo {
            String name;
            String address;
            String availability;
            LatLng latLng;
            int id;

            public ParkingLotInfo(String name, String address, String availability) {
                this.name = name;
                this.address = address;
                this.availability = availability;
            }
        }

        @Override
        protected List<ParkingLotInfo> doInBackground(Void... voids) {
            List<ParkingLotInfo> parkingLots = new ArrayList<>();
            List<String> parkingDetails = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://34.41.128.207:5000/parking-lots");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("FetchAddressAsyncTask", "HTTP error code: " + responseCode);
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    json.append(line).append('\n');
                }

                JSONObject jsonObject = new JSONObject(json.toString());
                Iterator<String> keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String lotName = keys.next(); // This is the parking lot name like "Blair Lot"
                    JSONObject lot = jsonObject.getJSONObject(lotName);
                    JSONArray addressArray = lot.getJSONArray("addresses");
                    String availability = lot.optString("availability", "unknown");

                    if (addressArray.length() > 0) {
                        String fullAddress = addressArray.getString(0);
                        String[] addressParts = fullAddress.split("\n");
                        String streetAddress = addressParts[0];

                        if (lotName.length() > 35) {
                            lotName = lotName.substring(0, 35);
                            // Optionally, trim to the last complete word
                            int lastSpaceIndex = lotName.lastIndexOf(' ');
                            //Log.d("Debug", "lastSpaceIndex"+lastSpaceIndex);
                            if (lastSpaceIndex > 0) {
                                lotName = lotName.substring(0, lastSpaceIndex);
                            }
                        }
                        Log.d("Debug", "Lotname "+ lotName + " address "+ streetAddress);

                        parkingLots.add(new ParkingLotInfo(lotName, fullAddress, availability));

                        String parkingInfo = lotName + "\n" + streetAddress + "\nCurrent Availability: " + availability;
                        parkingLotList.add(parkingInfo);
                    }
                }

            } catch (Exception e) {
                Log.e("FetchAddressAsyncTask", "Error: " + e.getMessage(), e);
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("FetchAddressAsyncTask", "Error closing stream", e);
                    }
                }
            }
            return parkingLots;
        }

        @Override
        protected void onPostExecute(List<ParkingLotInfo> parkingLots) {
            if (parkingLots != null && !parkingLots.isEmpty()) {
                int id = 0;
                // Update the UI, for example, update a list view with parking lot details
                for (ParkingLotInfo lot : parkingLots) {
                    lot.id = id++;
//                    String parkingDetails = lot.name + "\n" + lot.address + "\nCurrent Availability: " + lot.availability;
//                    parkingLotList.add(parkingDetails);
                    new GeocodeAsyncTask(mMap, lot).execute(lot.address);
                }
                if (parkingLotList != null && !parkingLotList.isEmpty()) {
                    ParkingListAdapter adapter = new ParkingListAdapter(getActivity(), parkingLotList, listLatLng);
                    listViewParking.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Debug", "parkingLotList is null or empty");
                }
            } else {
                Log.e("FetchAddressAsyncTask", "No parking details found");
            }
        }
    }







    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isTrafficEnabled = sharedPreferences.getBoolean("TrafficEnabled", false);
        boolean isPositionEnabled = sharedPreferences.getBoolean("PositionEnabled", false);
        mMap.setTrafficEnabled(isTrafficEnabled);

        readApi();
        LatLng madison = new LatLng(43.0745614, -89.407373);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madison, 14));

        isPositionEnabled= true;
        if (isPositionEnabled) {
            enableUserLocation();
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Initialize or update your locationCallback here if needed
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    addUserPositionMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }



    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }


    private void addUserPositionMarker(LatLng userLocation) {
        if (mMap != null && userLocation != null) {
            if (userPositionMarker != null) userPositionMarker.remove(); // Remove existing marker if present
            userPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(userLocation)
                    .draggable(true) // Set to false if you don't want the marker to be draggable
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_beenhere_24))); // Custom icon
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


}