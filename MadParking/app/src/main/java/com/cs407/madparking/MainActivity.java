package com.cs407.madparking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.gson.Gson;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private ImageView navigationIconImageView;
    private HomeViewModel homeViewModel;
    private StatisticsViewModel statisticsViewModel;

    private ParkingLotRepository parkingLotRepository;

    private ImageView titleIconImageView;

    private TextView toolbarTitle;

    static int lastActiveFragment = R.id.nav_host_fragment_activity_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the Toolbar after setting the content view
        toolbarTitle = findViewById(R.id.toolbar_title);


        // Initialize the Toolbar after setting the content view
        setSupportActionBar(findViewById(R.id.toolbar));

        titleIconImageView = findViewById(R.id.toolbar_image_left);
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
                lastActiveFragment = destination.getId();
            }
        });

        // Set click listeners for the icons
        setIconClickListeners();

        // Initialize ViewModel and Repository
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        parkingLotRepository = new ParkingLotRepository();

        // Load data and update ViewModel
        initStaticFragment();
    }

    protected void initStaticFragment() {
        // create view model by using reference to the repository
        statisticsViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new StatisticsViewModel(parkingLotRepository);
            }
        }).get(StatisticsViewModel.class);
    }


    private void updateNavigationIcon(int destinationId) {
        if (destinationId == R.id.navigation_home) {
            titleIconImageView.setImageDrawable(null);
            navigationIconImageView.setImageResource(R.drawable.baseline_settings_24);
            toolbarTitle.setText("Home Screen");
        } else if (destinationId == R.id.navigation_map) {
            titleIconImageView.setImageDrawable(null);
            navigationIconImageView.setImageResource(R.drawable.baseline_settings_24);
            toolbarTitle.setText("Map");
        } else if (destinationId == R.id.navigation_statistics) {
            titleIconImageView.setImageDrawable(null);
            navigationIconImageView.setImageResource(R.drawable.baseline_calendar_24);
            toolbarTitle.setText("Statistics");
        } else if (destinationId == R.id.navigation_settings) {
            titleIconImageView.setImageDrawable(null);
            navigationIconImageView.setImageDrawable(null);
            toolbarTitle.setText("General Settings");
        } else if (destinationId == R.id.navigation_date_selection) {
            titleIconImageView.setImageResource(R.drawable.baseline_calendar_24);
            navigationIconImageView.setImageDrawable(null);
            toolbarTitle.setText("Week Selection");
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
                        navController.navigate(R.id.navigation_date_selection);
                    }
                }
            }
        });
    }

}
