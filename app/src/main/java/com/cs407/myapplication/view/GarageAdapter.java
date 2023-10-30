package com.cs407.myapplication.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.myapplication.R;
import com.cs407.myapplication.data.GarageInfo;

import java.util.List;

public class GarageAdapter extends RecyclerView.Adapter<GarageAdapter.ViewHolder>{
    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull ViewGroup parent) {
            super(parent);
        }
    }

    private List<GarageInfo> garageList;
    public GarageAdapter(List<GarageInfo> garageList){
        this.garageList = garageList;

    }
    @NonNull
    @Override
    public GarageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_statistics, parent, false);
        return new ViewHolder((ViewGroup) itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GarageAdapter.ViewHolder holder, int position) {
        GarageInfo garageInfo = garageList.get(position);
    }

    @Override
    public int getItemCount() {
        return garageList.size();
    }
}
