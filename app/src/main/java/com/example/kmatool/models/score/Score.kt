package com.example.kmatool.models.score

data class Score(
    val subject: Subject,
    val firstComponentScore: String,
    val secondComponentScore: String,
    val examScore: String,
    val avgScore: String,
    val alphabetScore: String,
    var index: Int
)
