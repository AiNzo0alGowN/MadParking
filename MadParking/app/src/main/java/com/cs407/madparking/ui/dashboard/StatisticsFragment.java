package com.cs407.madparking.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cs407.madparking.R;
import com.cs407.madparking.databinding.FragmentStatisticsBinding;
import com.cs407.madparking.ui.dashboard.statis.adapter.GarageDataAdapter;

import java.util.Objects;

public class StatisticsFragment extends Fragment {
    private StatisticsViewModel statisticsViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;

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

        // init swipe refresh layout
        swipeRefreshLayout = view.findViewById(R.id.statics_swipe_refresh_layout);

        // set swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> statisticsViewModel.updateData());
        swipeRefreshLayout.setOnClickListener(v -> {
            int position = recyclerView.getChildAdapterPosition(v);
            Toast.makeText(getContext(), "Clicked " + position + " (" + ((GarageDataAdapter) Objects.requireNonNull(recyclerView.getAdapter())).getGarageData(position).getName() + ")", Toast.LENGTH_SHORT).show();
        });

        // 初始化 ViewModel
        statisticsViewModel.getParkingSpots().observe(getViewLifecycleOwner(), newData -> {
            // 当数据更改时，更新 Adapter
            ((GarageDataAdapter) Objects.requireNonNull(recyclerView.getAdapter())).setDataList(newData);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}