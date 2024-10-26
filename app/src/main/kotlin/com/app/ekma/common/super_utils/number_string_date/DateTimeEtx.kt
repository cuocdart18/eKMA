package com.app.ekma.common.super_utils.number_string_date

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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