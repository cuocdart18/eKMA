package com.example.kmatool.data.models

class Result<T>(
    val data: T?,
    val message: String,
    val statusCode: Int
)
