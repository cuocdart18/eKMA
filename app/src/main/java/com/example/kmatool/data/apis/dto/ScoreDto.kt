package com.example.kmatool.data.apis.dto

data class ScoreDto(
    val subject: SubjectDto,
    val firstComponentScore: String,
    val secondComponentScore: String,
    val examScore: String,
    val avgScore: String,
    val alphabetScore: String,
    var index: Int
)
