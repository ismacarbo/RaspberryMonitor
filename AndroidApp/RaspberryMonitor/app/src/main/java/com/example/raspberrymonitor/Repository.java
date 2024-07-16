package com.example.raspberrymonitor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class Repository {
    private static final String TAG = "Repository";
    private MutableLiveData<List<User>> dbRecords = new MutableLiveData<>();
    private MutableLiveData<SystemInfoResponse> systemInfo = new MutableLiveData<>();

    public LiveData<List<User>> getDbRecords() {
        return dbRecords;
    }

    public LiveData<SystemInfoResponse> getSystemInfo() {
        return systemInfo;
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
}
