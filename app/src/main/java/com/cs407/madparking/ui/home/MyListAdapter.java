package com.cs407.madparking.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.madparking.R;

import java.util.ArrayList;
import java.util.Map;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {

    private final Map<String, Object> mData;

    public MyListAdapter(Map<String, Object> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_parking_lot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String parkingLotName = new ArrayList<>(mData.keySet()).get(position);
        Map<?, ?> parkingLotDetails = (Map<?, ?>) mData.get(parkingLotName);

        String address = parkingLotDetails.get("addresses").toString().replace("[", "").replace("]", "");
        String operation = parkingLotDetails.get("hours").toString();
        String availability = parkingLotDetails.get("availability").toString();

        Log.d("T", parkingLotDetails.toString());
        holder.name.setText(parkingLotName + ":");
        holder.address.setText(address);
        holder.operation.setText(operation);
        holder.availability.setText("Current Availability: " + availability);
        if (parkingLotDetails.get("ev_charging") != null){
            holder.evIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;
        TextView operation;
        TextView availability;
        ImageView evIcon;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.garage_name_text_view);
            address = itemView.findViewById(R.id.garage_address_text_view);
            operation = itemView.findViewById(R.id.operation_hours_text_view);
            availability = itemView.findViewById(R.id.availability_text_view);
            evIcon = itemView.findViewById(R.id.electric_charging_icon);
        }
    }
}
