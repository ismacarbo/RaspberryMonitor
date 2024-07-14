package com.example.raspberrymonitor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("/api/db_records")
    Call<DbRecordsResponse> getDbRecords(@Header("Authorization") String token);

    @GET("/api/system_info")
    Call<SystemInfoResponse> getSystemInfo(@Header("Authorization") String token);
}
