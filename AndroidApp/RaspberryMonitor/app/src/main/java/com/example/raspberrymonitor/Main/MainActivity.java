package com.example.raspberrymonitor.Main;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.raspberrymonitor.DBRecords.FragmentDbRecords;
import com.example.raspberrymonitor.Logout.FragmentLogout;
import com.example.raspberrymonitor.Movements.FragmentMovements;
import com.example.raspberrymonitor.Movements.MovementsResponse;
import com.example.raspberrymonitor.R;
import com.example.raspberrymonitor.SystemInfo.FragmentSystemInfo;
import com.example.raspberrymonitor.Network.FragmentNetworkInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MainViewModel viewModel;
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 5000; // 5 seconds for testing purposes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Recupera il token da SharedPreferences
        String token = getSharedPreferences("prefs", MODE_PRIVATE)
                .getString("token", null);

        viewModel.setToken(token);

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentDbRecords())
                    .commit();
        }

        // Set up the handler for automatic updates
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                viewModel.fetchSystemInfo();
                viewModel.fetchMovements(); // Fetch movements data
                viewModel.fetchNetworkInfo(); // Fetch network data

                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        handler.post(runnable);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_db_records) {
                        selectedFragment = new FragmentDbRecords();
                    } else if (itemId == R.id.nav_system_info) {
                        selectedFragment = new FragmentSystemInfo();
                    } else if (itemId == R.id.nav_movements) {
                        selectedFragment = new FragmentMovements();
                    } else if (itemId == R.id.nav_network_info) {
                        selectedFragment = new FragmentNetworkInfo();
                    } else if (itemId == R.id.nav_logout) {
                        selectedFragment = new FragmentLogout();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
