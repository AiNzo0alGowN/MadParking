package com.cs407.madparking.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cs407.madparking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ListView listViewParking;
    private ArrayList<String> parkingLotList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewParking = view.findViewById(R.id.listViewParking);
        parkingLotList = new ArrayList<>();

        // Add your parking lot information here
        parkingLotList.add("Grainger Hall Garage (Lot 7)\n325 N.Brooks Street\nCurrent Availability: FULL");
        // Add more parking lot items similarly...

        // Setting up the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, parkingLotList);
        listViewParking.setAdapter(adapter);

        // Set up a click listener for the ListView items
        listViewParking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // You can use a switch statement or if-else blocks if there are multiple parking lots
                if (position == 0) { // Assuming 0 is the index for "Grainger Hall Garage (Lot 7)"
                    double latitude = 43.0731; // Replace with actual latitude
                    double longitude = -89.4012; // Replace with actual longitude
                    String label = "Grainger Hall Garage (Lot 7)";
                    String uriBegin = "geo:" + latitude + "," + longitude;
                    String query = latitude + "," + longitude + "(" + label + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
                // Add more cases for other parking lots
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Madison and move the camera
        LatLng madison = new LatLng(43.0745614, -89.407373);
        mMap.addMarker(new MarkerOptions().position(madison).title("Marker in Madison"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madison, 14));
    }
}
