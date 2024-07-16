package com.example.raspberrymonitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private TextView dbRecordsTextView;
    private TextView temperatureTextView;
    private TextView memoryTextView;
    private TextView diskTextView;
    private TextView powerTextView;
    private Button fetchDataButton;
    private Button logoutButton;
    private Spinner databaseSpinner;
    private WebView webView;
    private String token;
    private static final String TAG = "MainActivity";
    private Handler handler;
    private Runnable runnable;
    private final int UPDATE_INTERVAL = 10000; // 10 seconds

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
        logoutButton = findViewById(R.id.logoutButton);
        databaseSpinner = findViewById(R.id.databaseSpinner);
        webView = findViewById(R.id.webView);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Recupera il token da SharedPreferences
        token = getSharedPreferences("prefs", MODE_PRIVATE)
                .getString("token", null);

        viewModel.setToken(token);

        // Set up the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/chart.html");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("MyApplication", message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
            }
        });


        // Save the token in the WebView localStorage
        if (token != null) {
            webView.evaluateJavascript("localStorage.setItem('token', '" + token + "');", null);
        }

        // Configure the database spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.database_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        databaseSpinner.setAdapter(adapter);

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

                // Invia i dati alla WebView per aggiornare i grafici
                String jsCode = String.format("updateCharts(%s, %s, %s);",
                        systemInfoResponse.getTemperature(),
                        systemInfoResponse.getMemory(),
                        systemInfoResponse.getDisk());
                webView.evaluateJavascript(jsCode, null);
            }
        });

        fetchDataButton.setOnClickListener(v -> {
            String selectedDb = databaseSpinner.getSelectedItem().toString();
            viewModel.fetchDbRecords(selectedDb);
            viewModel.fetchSystemInfo();
        });

        logoutButton.setOnClickListener(v -> {
            logout();
        });

        // Set up the handler for automatic updates
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                viewModel.fetchSystemInfo();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void logout() {
        ApiService apiService = RetrofitInstance.getApiService();
        Call<LogoutResponse> call = apiService.logout("Bearer " + token);

        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Logout successful: " + response.body().getMsg());
                    // Clear the token from SharedPreferences
                    getSharedPreferences("prefs", MODE_PRIVATE)
                            .edit()
                            .remove("token")
                            .apply();
                    // Go back to LoginActivity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Log.e(TAG, "Logout failed: " + response.code() + ", " + response.message());
                    Toast.makeText(MainActivity.this, "Logout failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Log.e(TAG, "Logout error", t);
                Toast.makeText(MainActivity.this, "Logout error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
