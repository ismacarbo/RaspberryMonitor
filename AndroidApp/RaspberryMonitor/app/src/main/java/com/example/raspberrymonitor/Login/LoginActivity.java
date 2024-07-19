package com.example.raspberrymonitor.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.raspberrymonitor.API.ApiService;
import com.example.raspberrymonitor.API.RetrofitInstance;
import com.example.raspberrymonitor.Main.MainActivity;
import com.example.raspberrymonitor.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            login(username, password);
        });
    }

    private void login(String username, String password) {
        ApiService apiService = RetrofitInstance.getApiService();
        Call<LoginResponse> call = apiService.login(new LoginRequest(username, password));
        System.out.println(call.toString());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    Log.d(TAG, "Token received: " + token);
                    if (token != null) {
                        // Salva il token in SharedPreferences
                        getSharedPreferences("prefs", MODE_PRIVATE)
                                .edit()
                                .putString("token", token)
                                .apply();
                        // Vai alla MainActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, "Token is null");
                        Toast.makeText(LoginActivity.this, "Token is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Login failed: " + response.code() + ", " + response.message());
                    try {
                        Log.e(TAG, "Response body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login error", t);
                Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
