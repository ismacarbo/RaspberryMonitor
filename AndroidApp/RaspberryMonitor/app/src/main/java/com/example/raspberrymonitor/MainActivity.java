package com.example.raspberrymonitor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.widget.TextView;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private TextView dbRecordsTextView;
    private TextView temperatureTextView;
    private TextView memoryTextView;
    private TextView diskTextView;
    private TextView powerTextView;
    private Button fetchDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbRecordsTextView = findViewById(R.id.dbRecordsTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        memoryTextView = findViewById(R.id.memoryTextView);
        diskTextView = findViewById(R.id.diskTextView);
        powerTextView = findViewById(R.id.powerTextView);
        fetchDataButton = findViewById(R.id.fetchDataButton);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Recupera il token da SharedPreferences
        String token = getSharedPreferences("prefs", MODE_PRIVATE)
                .getString("token", null);

        viewModel.setToken(token);

        viewModel.getDbRecords().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    StringBuilder records = new StringBuilder("Records:\n");
                    for (User user : users) {
                        records.append(user.toString()).append("\n");
                    }
                    dbRecordsTextView.setText(records.toString());
                } else {
                    dbRecordsTextView.setText("No records found");
                }
            }
        });


        viewModel.getSystemInfo().observe(this, new Observer<SystemInfoResponse>() {
            @Override
            public void onChanged(SystemInfoResponse systemInfoResponse) {
                temperatureTextView.setText("Temperature: " + systemInfoResponse.getTemperature());
                memoryTextView.setText("Memory: " + systemInfoResponse.getMemory() + "%");
                diskTextView.setText("Disk: " + systemInfoResponse.getDisk() + "%");
                powerTextView.setText("Power: " + systemInfoResponse.getPower());
            }
        });

        fetchDataButton.setOnClickListener(v -> {
            viewModel.fetchDbRecords();
            viewModel.fetchSystemInfo();
        });
    }
}
