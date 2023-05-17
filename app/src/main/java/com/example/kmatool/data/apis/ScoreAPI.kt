package com.example.kmatool.data.apis

import com.example.kmatool.data.apis.dto.MiniStudentResult
import com.example.kmatool.data.apis.dto.PagePropsResult
import retrofit2.http.GET
import retrofit2.http.Path

interface ScoreAPI {

    /*@GET("/statistics")
    suspend fun getStatistics(): Result<Statistic>*/

    @GET("${ApiConfig.PROCESS_ENV}student/{studentId}.json")
    suspend fun getStudentStatistics(@Path("studentId") studentId: String): PagePropsResult

    /*@GET("/subject/{subjectId}")
    suspend fun getSubjectStatistics(@Path("subjectId") subjectId: String): Result<StatisticSubject>*/

    /*@GET("/subjects")
    suspend fun getSubjects(): Result<List<Subject>>*/

    @GET("api/search/{query}")
    suspend fun search(@Path("query") query: String): MiniStudentResult
}