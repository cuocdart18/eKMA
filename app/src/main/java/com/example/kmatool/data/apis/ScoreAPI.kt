package com.example.kmatool.data.apis

import com.example.kmatool.data.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScoreAPI {

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