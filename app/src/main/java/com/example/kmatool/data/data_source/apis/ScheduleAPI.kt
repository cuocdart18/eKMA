package com.example.kmatool.data.data_source.apis

import com.example.kmatool.data.data_source.apis.dto.ProfileDto
import com.example.kmatool.data.data_source.apis.dto.ScheduleDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleAPI {

    @GET("/schedule")
    suspend fun getPeriods(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean
    ): ScheduleDto

    @GET("/profile")
    suspend fun getProfile(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean
    ): ProfileDto
}