package com.example.kmatool.api_service

import com.example.kmatool.models.score.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiScoreService {

    @GET("/statistics")
    suspend fun getStatistics(): Result<Statistic>

    @GET("/student/{studentId}")
    suspend fun getStudentStatistics(@Path("studentId") studentId: String): Result<Student>

    @GET("/subject/{subjectId}")
    suspend fun getSubjectStatistics(@Path("subjectId") subjectId: String): Result<StatisticSubject>

    @GET("/subjects")
    suspend fun getSubjects(): Result<List<Subject>>

    @GET("/search")
    suspend fun search(@Query("query") data: String): Result<List<MiniStudent>>
}