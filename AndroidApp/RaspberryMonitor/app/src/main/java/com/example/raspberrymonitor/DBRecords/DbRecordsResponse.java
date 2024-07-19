package com.example.raspberrymonitor.DBRecords;

import java.util.List;

public class DbRecordsResponse {
    private List<User> records;

    public List<User> getRecords() {
        return records;
    }

    public void setRecords(List<User> records) {
        this.records = records;
    }
}
