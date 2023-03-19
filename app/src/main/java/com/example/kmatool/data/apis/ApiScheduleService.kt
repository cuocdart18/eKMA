package com.example.kmatool.data.apis

import com.example.kmatool.data.models.Profile
import com.example.kmatool.data.models.Schedule
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiScheduleService {

    @GET("/schedule")
    suspend fun getScheduleData(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean
    ): Schedule

    @GET("/profile")
    suspend fun getProfile(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean
    ): Profile
}