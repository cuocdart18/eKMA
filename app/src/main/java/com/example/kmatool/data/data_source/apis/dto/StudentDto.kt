package com.example.kmatool.data.data_source.apis.dto

import com.google.gson.annotations.SerializedName

data class StudentDto(
    val id: String,
    val name: String,
    @SerializedName("class")
    val classInSchool: String,
    val avgScore: Double,
    val passedSubjects: Int,
    val failedSubjects: Int,
    val scores: List<ScoreDto>
)
