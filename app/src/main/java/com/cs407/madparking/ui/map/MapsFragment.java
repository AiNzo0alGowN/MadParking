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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
                //mMap.addMarker(new MarkerOptions().position(latLng).title("Grainger Hall Garage (Lot 7)"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                // Updating the ListView with the parking lot information
//                parkingLotList.clear();
                parkingLotList.add("Grainger Hall Garage (Lot 7)\n325 N.Brooks Street\nCurrent Availability: FULL");

                //one more element in the lisview
                listLatLng.add(new LatLng(43.0745614, -89.407373));
                parkingLotList.add("Test1\ntest2\ntest3");
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

        // Setting up the adapter
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, parkingLotList);
//        listViewParking.setAdapter(adapter);

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

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isTrafficEnabled = sharedPreferences.getBoolean("TrafficEnabled", false);
        mMap.setTrafficEnabled(isTrafficEnabled);

        LatLng madison = new LatLng(43.0745614, -89.407373);
//        mMap.addMarker(new MarkerOptions().position(madison).title("Marker in Madison"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madison, 14));



        readApi();

        // Pass the GoogleMap object to the GeocodeAsyncTask
        new GeocodeAsyncTask(mMap).execute("325 N Brooks St");
    }
}
