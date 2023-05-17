package com.example.kmatool.common

import com.google.gson.Gson
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// Global method
fun convertPeriodToTime(data: String): String =
    when (data) {
        "1" -> "07:00"
//        "2" -> "7:00"
        "3" -> "09:25"
        "4" -> "09:35"
//        "5" -> "7:00"
        "6" -> "12:00"
        "7" -> "12:30"
//        "8" -> "7:00"
        "9" -> "14:55"
        "10" -> "15:05"
//        "11" -> "7:00"
        "12" -> "17:30"
        "13" -> "18:00"
//        "14" -> "7:00"
//        "15" -> "7:00"
        "16" -> "21:15"
        else -> "invalid data"
    }

/*
*   get
*   start time with key = "start"
*   end time with key = "end"
* */
fun convertPeriodsToStartEndTime(data: String): Map<String, String> {
    val periods = data
        .trim()
        .splitToSequence(',')
        .toList()
    return mapOf(
        "start" to convertPeriodToTime(periods[0]),
        "end" to convertPeriodToTime(periods[periods.size - 1])
    )
}

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

fun jsonObjectToString(data: Any): String {
    val gson = Gson()
    return gson.toJson(data)
}

inline fun <reified T> jsonStringToObject(data: String): T {
    val gson = Gson()
    return gson.fromJson(data, T::class.java)
}

fun toDateTime(date: LocalDate, time: LocalTime): LocalDateTime =
    LocalDateTime.of(date, time)