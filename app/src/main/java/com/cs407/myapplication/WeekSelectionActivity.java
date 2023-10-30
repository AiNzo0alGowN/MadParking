package com.cs407.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WeekSelectionActivity extends AppCompatActivity {
    private BottomNavigationView navigationIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_selection);
        navigationIconImageView = findViewById(R.id.nav_view);

        CalendarView calendarView = findViewById(R.id.calendarView);
        EditText editText = findViewById(R.id.et_pickDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month 从 0 开始，所以需要 +1
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            editText.setText(date);
        });

        Button confirmButton = findViewById(R.id.btn_confirm);
        confirmButton.setOnClickListener(v -> {
            // 在这里处理确认操作
            Toast.makeText(this, "Confirmed!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.setClass(WeekSelectionActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
    }
}