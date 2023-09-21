package com.app.ekma.data.models

data class User(
    val username: String,
    val password: String,
    val hashed: Boolean
)
