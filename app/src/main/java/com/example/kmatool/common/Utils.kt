package com.example.kmatool.common

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

fun LocalDate.toDayMonthYear(): String =
    this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

fun LocalTime.toHourMinute(): String =
    this.format(DateTimeFormatter.ofPattern("HH:mm"))

fun LocalDate.toYearMonth(): String = this.format(DateTimeFormatter.ofPattern("yyyy-MM"))

fun LocalDateTime.toMilli(): Long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun String.toLocalDate(): LocalDate =
    LocalDate.parse(this, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

fun String.toLocalTime(): LocalTime =
    LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))

fun toDateTime(date: LocalDate, time: LocalTime): LocalDateTime =
    LocalDateTime.of(date, time)

fun Int.formatDoubleChar(): String {
    return if (this >= 10) "$this"
    else "0$this"
}

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