package com.example.raspberrymonitor.API;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import com.example.raspberrymonitor.DBRecords.DbRecordsResponse;
import com.example.raspberrymonitor.Movements.MovementsResponse;
import com.example.raspberrymonitor.SystemInfo.SystemInfoResponse;
import com.example.raspberrymonitor.DBRecords.User;
import com.example.raspberrymonitor.Movements.MovementRequest;

public class Repository {
    private static final String TAG = "Repository";
    private MutableLiveData<List<User>> dbRecords = new MutableLiveData<>();
    private MutableLiveData<SystemInfoResponse> systemInfo = new MutableLiveData<>();
    private MutableLiveData<List<MovementsResponse.Movement>> movements = new MutableLiveData<>();

    public LiveData<List<User>> getDbRecords() {
        return dbRecords;
    }

    public LiveData<SystemInfoResponse> getSystemInfo() {
        return systemInfo;
    }

    public LiveData<List<MovementsResponse.Movement>> getMovements() {
        return movements;
    }

    public void fetchDbRecords(String token, String dbName) {
        RetrofitInstance.getApiService().getDbRecords("Bearer " + token, dbName).enqueue(new Callback<DbRecordsResponse>() {
            @Override
            public void onResponse(Call<DbRecordsResponse> call, Response<DbRecordsResponse> response) {
                if (response.isSuccessful()) {
                    DbRecordsResponse dbRecordsResponse = response.body();
                    if (dbRecordsResponse != null) {
                        dbRecords.setValue(dbRecordsResponse.getRecords());
                        Log.d(TAG, "DB records fetched: " + dbRecordsResponse.getRecords());
                    } else {
                        Log.e(TAG, "Response body is null");
                    }
                } else {
                    Log.e(TAG, "Error code: " + response.code() + ", " + response.message());
                    try {
                        Log.e(TAG, "Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DbRecordsResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching DB records", t);
            }
        });
    }

    public void fetchSystemInfo(String token) {
        RetrofitInstance.getApiService().getSystemInfo("Bearer " + token).enqueue(new Callback<SystemInfoResponse>() {
            @Override
            public void onResponse(Call<SystemInfoResponse> call, Response<SystemInfoResponse> response) {
                if (response.isSuccessful()) {
                    systemInfo.setValue(response.body());
                } else {
                    Log.e(TAG, "Error code: " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SystemInfoResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching system info", t);
            }
        });
    }

    public void fetchMovements(String token) {
        MovementRequest movementRequest = new MovementRequest("Example movement data");
        RetrofitInstance.getApiService().getMovements("Bearer " + token, movementRequest).enqueue(new Callback<MovementsResponse>() {
            @Override
            public void onResponse(Call<MovementsResponse> call, Response<MovementsResponse> response) {
                if (response.isSuccessful()) {
                    MovementsResponse movementsResponse = response.body();
                    if (movementsResponse != null) {
                        movements.setValue(movementsResponse.getMovements());
                        Log.d(TAG, "Movements fetched: " + movementsResponse.getMovements());
                    } else {
                        Log.e(TAG, "Response body is null");
                    }
                } else {
                    Log.e(TAG, "Error code: " + response.code() + ", " + response.message());
                    try {
                        Log.e(TAG, "Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovementsResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching movements", t);
            }
        });
    }
}
