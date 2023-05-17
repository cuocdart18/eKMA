package com.example.kmatool.data.models

data class Student(
    val id: String,
    val name: String,
    val classInSchool: String,
    val avgScore: Double,
    val passedSubjects: Int,
    val failedSubjects: Int,
    val scores: List<Score>
)
