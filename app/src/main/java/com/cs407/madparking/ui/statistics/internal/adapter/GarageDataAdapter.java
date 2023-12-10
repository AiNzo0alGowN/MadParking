package com.cs407.madparking.ui.statistics.internal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.madparking.R;
import com.cs407.madparking.api.GarageData;
import com.cs407.madparking.api.WeekCountData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GarageDataAdapter extends RecyclerView.Adapter<GarageDataAdapter.GarageDataViewHolder> {
    private List<WeekCountData> dataList = new ArrayList<>();

    public static class GarageDataViewHolder extends RecyclerView.ViewHolder {
        public TextView garageNameTextView;
        public TextView garagePriceTextView;

        public GarageDataViewHolder(View itemView) {
            super(itemView);
            garageNameTextView = itemView.findViewById(R.id.garadge_name);
            garagePriceTextView = itemView.findViewById(R.id.garadge_price);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void bind(WeekCountData garageData) {
            garageNameTextView.setText(garageData.getGarageName());
            garagePriceTextView.setText("$" + String.format("%.2f", garageData.getPrice()));
        }
    }

    public void setDataList(List<WeekCountData> dataList) {
        this.dataList = new ArrayList<>(dataList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GarageDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GarageDataViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.garadge_list_item,
                                parent,
                                false));
    }

    @Override
    public void onBindViewHolder(@NonNull GarageDataViewHolder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
