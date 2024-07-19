package com.example.raspberrymonitor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

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

    public LiveData<List<MovementsResponse.Movement>> getMovements() {
        return repository.getMovements();
    }

    public void fetchDbRecords(String dbName) {
        repository.fetchDbRecords(token, dbName);
    }

    public void fetchSystemInfo() {
        repository.fetchSystemInfo(token);
    }

    public void fetchMovements(String token) {
        repository.fetchMovements(token);
    }
}
