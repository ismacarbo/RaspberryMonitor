package com.example.raspberrymonitor.Main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import com.example.raspberrymonitor.API.Repository;
import com.example.raspberrymonitor.DBRecords.User;
import com.example.raspberrymonitor.Movements.MovementsResponse;
import com.example.raspberrymonitor.SystemInfo.SystemInfoResponse;

public class MainViewModel extends ViewModel {
    private Repository repository = new Repository();
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public LiveData<List<User>> getDbRecords() {
        return repository.getDbRecords();
    }

    public LiveData<SystemInfoResponse> getSystemInfo() {
        return repository.getSystemInfo();
    }

    public LiveData<MovementsResponse> getMovements() {
        return repository.getMovements();
    }

    public void fetchDbRecords(String dbName) {
        repository.fetchDbRecords(token, dbName);
    }

    public void fetchSystemInfo() {
        repository.fetchSystemInfo(token);
    }

    public void fetchMovements() {
        repository.fetchMovements(token);
    }
}
