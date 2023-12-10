package com.cs407.madparking.ui.statistics.internal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs407.madparking.MadParking;
import com.cs407.madparking.R;
import com.cs407.madparking.databinding.FragmentStatisticsBinding;
import com.cs407.madparking.ui.statistics.StatisticsViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class NeedParkedFragment extends Fragment {

    private TextView textView;
    private StatisticsViewModel statisticsViewModel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel = new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        return inflater.inflate(R.layout.fragment_need_parked, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button confirmButton = view.findViewById(R.id.need_parked_btn);

        // add confirm button listener
        confirmButton.setOnClickListener(v -> {
            // make sure the fragment is changed after better parking is found
            if (statisticsViewModel.getNearestGarage().getValue() == null) {
                Toast.makeText(getContext(), "Please wait for looking parking", Toast.LENGTH_SHORT).show();
                return;
            }

            // change fragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("time", LocalDateTime.now());
            OnParkedFragment frag = new OnParkedFragment();
            frag.setArguments(bundle);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.parked_status_container, frag)
                    .addToBackStack(null)
                    .commit();
        });

        // setup text
        textView = view.findViewById(R.id.tv_parked_location);
        statisticsViewModel.getNearestGarage().observe(getViewLifecycleOwner(), garage -> {
            Log.d("NeedParkedFragment", "onViewCreated: " + garage);
            if (garage == null) {
                textView.setText("No garage found");
            } else {
                textView.setText(garage.getName());
            }
        });
    }
}