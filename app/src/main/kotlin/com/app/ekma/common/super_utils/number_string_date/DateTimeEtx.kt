package com.app.ekma.common.super_utils.number_string_date

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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

fun Long.utcToLocalLong(): Long {
    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utcCalendar.timeInMillis = this
    val localTimeZone = TimeZone.getDefault()
    val localCalendar = Calendar.getInstance(localTimeZone)
    localCalendar.timeInMillis = utcCalendar.timeInMillis
    return localCalendar.timeInMillis
}

fun Long.convertTimeSessionUpcoming(): String {
    val messageCal = Calendar.getInstance()
    messageCal.timeInMillis = this.utcToLocalLong()

    val now = Calendar.getInstance().timeInMillis
    val difference = now - this

    if (difference < 0) {
        return ""
    }
    val totalSeconds = difference / 1000
    val minutes = difference / (1000 * 60) % 60
    val hours = difference / (1000 * 60 * 60) % 24
    val days = difference / (1000 * 60 * 60 * 24)

    val format: String = when {
        days > 7 -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(this)
        days > 0 -> "${days}d"
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> "${totalSeconds}s" // Use string resource for minutes
    }
    return format
}