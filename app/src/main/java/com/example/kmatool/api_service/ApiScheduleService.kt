package com.example.kmatool.api_service

import com.example.kmatool.models.schedule.Profile
import com.example.kmatool.models.schedule.Schedule
import kotlinx.coroutines.Deferred
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