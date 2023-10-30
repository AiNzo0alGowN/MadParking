package com.cs407.retrofit_test;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ParkingLotRepository repository;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text1);  // 获取 TextView 的引用

        repository = new ParkingLotRepository();

        // Get list of parking lots
        repository.getParkingLots(new Callback<ParkingLotList>() {
            @Override
            public void onResponse(Call<ParkingLotList> call, Response<ParkingLotList> response) {
                if (response.isSuccessful()) {
                    ParkingLotList parkingLotList = response.body();

                    Log.d("MainActivity", "Parking lots: " + parkingLotList.parkingLots);

                    // Updated UI to show parking lot listings
                    StringBuilder text = new StringBuilder();  // Use StringBuilder to store text
                    for (ParkingLot parkingLot : parkingLotList.parkingLots) {
                        text.append("ID: ").append(parkingLot.id).append("\n");
                        text.append("Name: ").append(parkingLot.name).append("\n");
                        text.append("Address: ").append(parkingLot.address).append("\n");
                        text.append("Available Spots: ").append(parkingLot.availableSpots).append("\n\n");
                    }
                    // Updating TextView text in the main thread
                    runOnUiThread(() -> textView.setText(text.toString()));
                }
            }

            @Override
            public void onFailure(Call<ParkingLotList> call, Throwable t) {
                // 显示错误信息
                Log.e("MainActivity", "API request failed", t);
            }
        });

    }
}