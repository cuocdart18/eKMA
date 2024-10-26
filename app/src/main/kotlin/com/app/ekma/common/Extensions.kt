package com.app.ekma.common

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = NAME_DATASTORE_PREFS)

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

fun Int.formatDoubleChar(): String {
    return if (this >= 10) "$this"
    else "0$this"
}

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun <T> List<T>.chunkList(chunkSize: Int): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var index = 0
    while (index < size) {
        result.add(subList(index, kotlin.math.min(index + chunkSize, size)))
        index += chunkSize
    }
    return result
}