package com.cs407.madparking.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs407.madparking.R;
import com.cs407.madparking.databinding.FragmentStatisticsBinding;
import com.cs407.madparking.ui.dashboard.statis.adapter.GarageDataAdapter;

import java.util.Objects;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private StatisticsViewModel statisticsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);
        // hook rv
        return FragmentStatisticsBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 RecyclerView 和 Adapter
        RecyclerView recyclerView = view.findViewById(R.id.garages_list);
        recyclerView.setAdapter(new GarageDataAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化 ViewModel
        statisticsViewModel.getParkingSpots().observe(getViewLifecycleOwner(), newData -> {
            // 当数据更改时，更新 Adapter
            ((GarageDataAdapter) Objects.requireNonNull(recyclerView.getAdapter())).setDataList(newData);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}