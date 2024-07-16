package com.example.raspberrymonitor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("/api/db_records")
    Call<DbRecordsResponse> getDbRecords(@Header("Authorization") String token, @Query("db") String dbName);

    @GET("/api/system_info")
    Call<SystemInfoResponse> getSystemInfo(@Header("Authorization") String token);

    @POST("/logout")
    Call<LogoutResponse> logout(@Header("Authorization") String token);
}
