package com.example.kmatool.data.models

import com.google.gson.annotations.SerializedName

data class Student(
    val id: String,
    val name: String,
    @SerializedName("class")
    val classInSchool: String,
    val avgScore: Double,
    val passedSubjects: Int,
    val failedSubjects: Int,
    val scores: List<Score>
)
