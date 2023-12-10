package com.cs407.madparking.ui.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cs407.madparking.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ParkingListAdapter extends ArrayAdapter<String> {
    private List<LatLng> latLngList;
    public ParkingListAdapter(Context context, ArrayList<String> parkingLots, List<LatLng> latLngList) {
        super(context, 0, parkingLots);
        this.latLngList = latLngList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_parking, parent, false);
        }

        // Get the data item for this position
        String parkingLot = getItem(position);

        // Lookup view for data population
        TextView textViewParkingInfo = convertView.findViewById(R.id.textViewParkingInfo);
        Button buttonGoHere = convertView.findViewById(R.id.buttonGoHere);

        // Populate the data into the template view using the data object
        textViewParkingInfo.setText(parkingLot);
        buttonGoHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < latLngList.size()) {
                    LatLng selectedLocation = latLngList.get(position);
                    Log.d("Debug", "LatLng"+selectedLocation);
                    startNavigation(getContext(), selectedLocation);
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private void startNavigation(Context context, LatLng location) {
        String navigationUri = "google.navigation:q=" + location.latitude + "," + location.longitude;
        Uri gmmIntentUri = Uri.parse(navigationUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }




}
