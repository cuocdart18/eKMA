package com.app.ekma.data.models

data class Score(
    val subject: Subject,
    val firstComponentScore: String,
    val secondComponentScore: String,
    val examScore: String,
    val avgScore: String,
    val alphabetScore: String,
    var index: Int
)
