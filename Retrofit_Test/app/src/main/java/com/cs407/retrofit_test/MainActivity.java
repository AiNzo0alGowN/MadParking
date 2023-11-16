package com.cs407.retrofit_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.TextView;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text1);

        ParkingLotRepository repository = new ParkingLotRepository();
        repository.getParkingLots(this);
    }

    public void updateTextViewWithParkingData(Map<String, Object> data) {
        runOnUiThread(() -> {
            textView.setText(data.toString());
        });
    }
}