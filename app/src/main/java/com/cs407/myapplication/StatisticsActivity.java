package com.cs407.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.cs407.myapplication.data.GarageInfo;
import com.cs407.myapplication.statics.NeedParkedFragment;
import com.cs407.myapplication.view.GarageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatisticsActivity extends AppCompatActivity {

    private RecyclerView rvGarages;
    private List<GarageInfo> garageList;
    private GarageAdapter garageAdapter;

    private AppCompatImageButton calendarButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        rvGarages = findViewById(R.id.rvGarages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvGarages.setLayoutManager(layoutManager);

        garageList = new ArrayList<GarageInfo>();
        garageList.add(new GarageInfo("Grainger Hall Garage (Lot 7)", 17));

        garageAdapter = new GarageAdapter(garageList);
        rvGarages.setAdapter(garageAdapter);

        setContentView(R.layout.activity_statistics);

        calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> toWeekSelectionActivity(savedInstanceState));

        FragmentManager fragmentManager  = getSupportFragmentManager();


        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, NeedParkedFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("showing First")
                .commit();
    }

    protected void toWeekSelectionActivity(Bundle savedInstanceState) {
        Intent intent = new Intent();
        intent.setClass(StatisticsActivity.this, WeekSelectionActivity.class);
        startActivity(intent);
    }
}