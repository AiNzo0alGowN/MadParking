package com.cs407.madparking.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cs407.madparking.api.GarageData;
import com.cs407.madparking.api.WeekCountData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;

import com.cs407.madparking.MadParking;
import com.cs407.madparking.MadParkingDB;
import com.cs407.madparking.R;
import com.cs407.madparking.api.ParkingData;
import com.cs407.madparking.databinding.FragmentStatisticsBinding;
import com.cs407.madparking.ui.statistics.internal.adapter.GarageDataAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatisticsFragment extends Fragment {
    private final static List<String> WEEKDAYS = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

    private LocalDate date = null;
    private MadParkingDB db;
    private RecyclerView recyclerView;
    private BarChart lineChart;
    private StatisticsViewModel statisticsViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // get view model
        statisticsViewModel =
                new ViewModelProvider(requireActivity()).get(StatisticsViewModel.class);

        // update date to current data if null
        if (date == null) {
            date = LocalDate.now();
        }

        // get db
        db = ((MadParking) requireActivity().getApplication()).getDb();

        // hook rv
        return FragmentStatisticsBinding.inflate(inflater, container, false).getRoot();
    }

    private void initChart(){
        assert lineChart != null;
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(WEEKDAYS));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        lineChart.invalidate();
    }

    private float CountPrice(List<ParkingData> data, Map<String, GarageData> dataMap) {
        float price = 0;
        for (ParkingData parkingData : data) {
            if (dataMap.get(parkingData.getGarageName()) != null) {
                price += (dataMap.get(parkingData.getGarageName()).getGaragePrice().getRate() * parkingData.totalHours());
            }
        }
        return price;
    }

    private void updateAdnRefreshChart(List<List<ParkingData>> data, Map<String, GarageData> dataMap) {
        // 创建数据点
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            entries.add(new BarEntry(i, CountPrice(data.get(i), dataMap)));
        }

        // 使用数据点创建一个数据集
        BarDataSet dataSet = new BarDataSet(entries, ""); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        // 将数据附加到图表
        lineChart.setData(new BarData(dataSet));
        lineChart.invalidate(); // 刷新图表
    }

    private void UpdateFragInfo() {
        // update parking data
        statisticsViewModel.updateData(swipeRefreshLayout);

        // update bar chart
        statisticsViewModel.getParkingSpots().observe(getViewLifecycleOwner(), newData -> {
            // looking for nearest monday by date var
            LocalDate monday = date;
            while (monday.getDayOfWeek().getValue() != 1) {
                monday = monday.minusDays(1);
            }

            // pull the parking data from db
            List<List<ParkingData>> data = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                data.add(db.getParkingDataByDate(monday.plusDays(i)));
            }

            // update chart
            updateAdnRefreshChart(data, newData);

            // update this to mp
            Map<String, List<ParkingData>> TmpData = new HashMap<>();
            for (List<ParkingData> parkingData : data) {
                for (ParkingData parkingData1 : parkingData) {
                    if (TmpData.containsKey(parkingData1.getGarageName())) {
                        TmpData.get(parkingData1.getGarageName()).add(parkingData1);
                    } else {
                        List<ParkingData> tmp = new ArrayList<>();
                        tmp.add(parkingData1);
                        TmpData.put(parkingData1.getGarageName(), tmp);
                    }
                }
            }

            // use those data update rv
            List<WeekCountData> finalData = new ArrayList<>();
            for (String s : TmpData.keySet()) {
                finalData.add(new WeekCountData(s, CountPrice(TmpData.get(s), newData)));
            }

            ((GarageDataAdapter) Objects.requireNonNull(recyclerView.getAdapter())).setDataList(finalData);
        });
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check back result
        getParentFragmentManager().setFragmentResultListener("request_date", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                date = (LocalDate) bundle.getSerializable("date");
                Log.d("StatisticsFragment", "onFragmentResult: " + date);
            }
        });

        // init RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.garages_list);
        recyclerView.setAdapter(new GarageDataAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // init line chart
        lineChart = view.findViewById(R.id.chart);
        initChart();

        // init swipe refresh layout
        swipeRefreshLayout = view.findViewById(R.id.statics_swipe_refresh_layout);

        // set swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::UpdateFragInfo);

        UpdateFragInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}