package com.example.raspberrymonitor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainViewModel extends ViewModel {
    private Repository repository = new Repository();
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public LiveData<List<User>> getDbRecords() {
        return repository.getDbRecords();
    }

    public LiveData<SystemInfoResponse> getSystemInfo() {
        return repository.getSystemInfo();
    }

    public void fetchDbRecords() {
        repository.fetchDbRecords(token);
    }

    public void fetchSystemInfo() {
        repository.fetchSystemInfo(token);
    }
}
