package com.cs407.madparking.ui.statistics.internal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cs407.madparking.R;

import java.time.LocalDate;

public class DateTimeFragment extends Fragment {

    private LocalDate time;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inflate the layout for this fragment
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        EditText editText = view.findViewById(R.id.et_pickDate);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            time = LocalDate.of(year, month + 1, dayOfMonth);
            editText.setText(date);
        });


        Button confirmButton = view.findViewById(R.id.date_selection_btn_confirm);
        confirmButton.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("date", time);
                getParentFragmentManager().setFragmentResult("request_date", bundle);
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_time, container, false);
    }
}