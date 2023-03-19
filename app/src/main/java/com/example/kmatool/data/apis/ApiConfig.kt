package com.example.kmatool.data.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_SCORE_URL = "https://kma.lucasdang.me"
    private const val BASE_SCHEDULE_URL = "https://kma-score-api-clone.vercel.app"

    private val retrofitScore = Retrofit.Builder()
        .baseUrl(BASE_SCORE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitSchedule = Retrofit.Builder()
        .baseUrl(BASE_SCHEDULE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiScoreService = retrofitScore.create(ApiScoreService::class.java)
    val apiScheduleService = retrofitSchedule.create(ApiScheduleService::class.java)
}