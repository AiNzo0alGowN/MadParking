package com.cs407.madparking.ui.map;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cs407.madparking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ListView listViewParking;
    private ArrayList<String> parkingLotList;
    private List<LatLng> listLatLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    //used to add the marker from the given address
    private class GeocodeAsyncTask extends AsyncTask<String, Void, LatLng> {
        private GoogleMap taskMap;

        public GeocodeAsyncTask(GoogleMap map) {
            this.taskMap = map;
        }
        @Override
        protected LatLng doInBackground(String... addresses) {
            try {
                String address = addresses[0];
                String apiKey = "AIzaSyCzcC0Gh80sKlmbJgeIV9YkqkMRsWV4uPM"; // Replace with your actual API key
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
                listLatLng.add(latLng);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listViewParking.getAdapter();
                adapter.notifyDataSetChanged();
            }
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewParking = view.findViewById(R.id.listViewParking);
        parkingLotList = new ArrayList<>();

        listLatLng = new ArrayList<>();

        ParkingListAdapter adapter = new ParkingListAdapter(getActivity(), parkingLotList, listLatLng);
        listViewParking.setAdapter(adapter);


        // Set up a click listener for the ListView items
        listViewParking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check if the listLatLng has an item at the clicked position
                if (position < listLatLng.size()) {
                    LatLng selectedLocation = listLatLng.get(position);
                    addMarkerOnMap(selectedLocation);

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
            mMap.addMarker(new MarkerOptions().position(location).title("Selected Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
    }

    private void readApi(){
        new FetchAddressAsyncTask().execute();
    }


    private class FetchAddressAsyncTask extends AsyncTask<Void, Void, List<String>> {
        private List<String> streetAddresses = new ArrayList<>();

        @Override
        protected List<String> doInBackground(Void... voids) {
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
                        String streetAddress = addressParts[0]; // Extract only the street address

                        streetAddresses.add(streetAddress);

                        String parkingInfo = lotName + "\n" + streetAddress + "\nCurrent Availability: " + availability;
                        parkingDetails.add(parkingInfo);
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
            return parkingDetails;
        }

        @Override
        protected void onPostExecute(List<String> parkingDetails) {
            if (parkingDetails != null && !parkingDetails.isEmpty()) {
                parkingLotList.clear();
                parkingLotList.addAll(parkingDetails);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listViewParking.getAdapter();
                adapter.notifyDataSetChanged();
            } else {
                Log.e("FetchAddressAsyncTask", "No parking details found");
            }

            for (String address : streetAddresses) {
                new GeocodeAsyncTask(mMap).execute(address);
            }
        }
    }







    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isTrafficEnabled = sharedPreferences.getBoolean("TrafficEnabled", false);
        mMap.setTrafficEnabled(isTrafficEnabled);

        LatLng madison = new LatLng(43.0745614, -89.407373);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madison, 14));



        readApi();

        
    }
}
