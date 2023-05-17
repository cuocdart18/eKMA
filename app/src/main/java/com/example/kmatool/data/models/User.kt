package com.example.kmatool.data.models

data class User(
    val username: String,
    val password: String,
    val hashed: Boolean
)
