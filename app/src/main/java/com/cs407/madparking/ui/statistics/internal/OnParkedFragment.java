package com.cs407.madparking.ui.statistics.internal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs407.madparking.MadParking;
import com.cs407.madparking.MadParkingDB;
import com.cs407.madparking.R;
import com.cs407.madparking.api.GarageData;
import com.cs407.madparking.ui.statistics.StatisticsViewModel;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class OnParkedFragment extends Fragment {

    private LocalDateTime time;
    private TextView locationTextView;
    private TextView startTimeTextView;
    private TextView priceTextView;

    private StatisticsViewModel statisticsViewModel;


    private final Handler handler = new Handler();
    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            updateData();
            handler.postDelayed(this, 1000);
        }
    };

    private void startRepeatingTask() {
        updateTask.run();
    }

    private void stopRepeatingTask() {
        handler.removeCallbacks(updateTask);
    }

    @Override
    public void onResume() {
        super.onResume();
        startRepeatingTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button confirmButton = view.findViewById(R.id.on_parking_btn);
        confirmButton.setOnClickListener(v -> {
            // get MadParkingDB
            MadParkingDB db = ((MadParking) requireActivity().getApplication()).getDb();

            // insert parking data
            db.addParkingData(
                    LocalDate.now(),
                    locationTextView.getText().toString(),
                    time.toLocalTime(),
                    LocalTime.now()
            );

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.parked_status_container, NeedParkedFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        });

        // setup start time
        startTimeTextView = view.findViewById(R.id.tv_start_time);
        startTimeTextView.setText("Start at:" + time.toString());

        // setup price
        priceTextView = view.findViewById(R.id.tv_price);

        // setup location
        locationTextView = view.findViewById(R.id.tv_parked_location);
        statisticsViewModel.getNearestGarage().observe(getViewLifecycleOwner(), garage -> {
            Log.d("NeedParkedFragment", "onViewCreated: " + garage);
            if (garage == null) {
                locationTextView.setText("No garage found");
            } else {
                locationTextView.setText(garage.getName());
            }
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateData() {
        if (priceTextView != null && locationTextView != null && statisticsViewModel.getParkingSpots().getValue() != null) {
            GarageData data = statisticsViewModel.getParkingSpots().getValue().get(locationTextView.getText().toString());
            Duration parkingDuration = Duration.between(time.toLocalTime(), LocalTime.now());
            priceTextView.setText("Price: $" + String.format("%.2f", data.getGaragePrice().getRate() * parkingDuration.get(ChronoUnit.SECONDS) / 360.0));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            time = (LocalDateTime) args.getSerializable("time");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_parked, container, false);
    }
}