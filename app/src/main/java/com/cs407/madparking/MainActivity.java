package com.cs407.madparking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.cs407.madparking.api.GetParkingLotsResp;
import com.cs407.madparking.api.data.GarageData;
import com.cs407.madparking.databinding.ActivityMainBinding;
import com.cs407.madparking.ui.dashboard.StatisticsViewModel;
import com.cs407.madparking.ui.home.HomeViewModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private ImageView navigationIconImageView;
    private Toolbar toolbar;
    private HomeViewModel homeViewModel;
    private ParkingLotRepository parkingLotRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the Toolbar after setting the content view
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationIconImageView = findViewById(R.id.navigation_icon);
        // Initialize the NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Setup BottomNavigationView with the NavController
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Add destination change listener
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                updateNavigationIcon(destination.getId());
            }
        });

        // Set click listeners for the icons
        setIconClickListeners();

        // Initialize ViewModel and Repository
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        parkingLotRepository = new ParkingLotRepository();

        // Load data and update ViewModel
        loadData();
    }

    private void loadData() {
        parkingLotRepository.getParkingLots(new Callback<GetParkingLotsResp>() {
            StatisticsViewModel statisticsViewModel = new ViewModelProvider(MainActivity.this).get(StatisticsViewModel.class);

            @Override
            public void onResponse(Call<GetParkingLotsResp> call, Response<GetParkingLotsResp> response) {
                if (response.isSuccessful()) {
                    // Here you can format the data as needed
                    homeViewModel.updateText(response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetParkingLotsResp> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Failed to load data: ", t);
            }
        });
    }


    private void updateNavigationIcon(int destinationId) {
        if (destinationId == R.id.navigation_home) {
            navigationIconImageView.setImageResource(R.drawable.baseline_settings_24);
            toolbar.setTitle("Home Screen");
        } else if (destinationId == R.id.navigation_map) {
            navigationIconImageView.setImageResource(R.drawable.baseline_settings_24);
            toolbar.setTitle("Map");
        } else if (destinationId == R.id.navigation_statistics) {
            navigationIconImageView.setImageResource(R.drawable.baseline_calendar_24);
            toolbar.setTitle("Statistics");
        } else if (destinationId == R.id.navigation_settings) {
            navigationIconImageView.setImageDrawable(null);
            toolbar.setTitle("General Settings");
        }
    }


    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void setIconClickListeners() {
        navigationIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Determine the current destination
                NavDestination currentDestination = navController.getCurrentDestination();
                if (currentDestination != null) {
                    int destinationId = currentDestination.getId();
                    // Define different actions based on the current destination
                    if (destinationId == R.id.navigation_home) {
                        showToast("Home Setting icon clicked!");
                    } else if (destinationId == R.id.navigation_map) {
                        showToast("Map Setting icon clicked!");
                    } else if (destinationId == R.id.navigation_statistics) {
                        showToast("Statistic icon clicked!");
                    }
                }
            }
        });
    }

}
