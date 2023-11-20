package com.app.ekma.data.data_source.apis

import com.app.ekma.data.data_source.apis.dto.FcmDataMessageDto
import com.app.ekma.data.data_source.apis.dto.MessageResult
import com.app.ekma.data.data_source.apis.dto.ProfileDto
import com.app.ekma.data.data_source.apis.dto.ScheduleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EKmaAPI {

    @GET("/auth")
    suspend fun auth(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean
    ): MessageResult

    @GET("/schedule")
    suspend fun getPeriods(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean
    ): ScheduleDto

    @GET("/schedule-with-semester-code")
    suspend fun getPeriods(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean,
        @Query("semesterCode") semesterCode: String
    ): ScheduleDto

    @GET("/profile")
    suspend fun getProfile(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("hashed") hashed: Boolean
    ): ProfileDto

    @GET("/semester-codes")
    suspend fun getSemesterCodes(): List<String>

    @POST("/call-invitation")
    suspend fun sendCallInvitationMessage(
        @Body fcmDataMessageDto: FcmDataMessageDto
    )
}