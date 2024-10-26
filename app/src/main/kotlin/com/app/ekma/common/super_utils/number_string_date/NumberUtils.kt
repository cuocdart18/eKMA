package com.app.ekma.common.super_utils.number_string_date

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.IntRange
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.random.Random

val Number.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Number.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()


val Number.dpf get() = dp.toFloat()

val Number.spf get() = sp.toFloat()

val Int.length get() = toString().length

// UCLN
fun Int.gcd(n: Int): Int {
    return if (n == 0) this else n.gcd(this % n)
}

//1 -> "01", 10 -> "10"
fun Int.twoDigitTime() = if (this < 10) "0" + toString() else toString()

//C -> F
fun Double.celToFah(): Double = (this * 1.8) + 32

//F -> C
fun Double.fahToCel(): Double = (this - 32) * 5 / 9

fun Number.round(@IntRange(from = 1L) decimalCount: Int): String {
    val expression = StringBuilder().append("#.")
    repeat((1..decimalCount).count()) { expression.append("#") }
    val formatter = DecimalFormat(expression.toString())
    formatter.roundingMode = RoundingMode.HALF_UP
    return formatter.format(this)
}

fun Int.forEach(callback: (i: Int) -> Unit) {
    for (i in 0 until this) {
        callback(i)
    }
}

fun randomNumber(fromNo: Int = 0, toNo: Int = 1000, noToBeGenerated: Int = 20) =
    List(noToBeGenerated) { Random.nextInt(fromNo, toNo) }

fun randomNumber(fromNo: Double = 0.0, toNo: Double = 1000.0, noToBeGenerated: Int = 20) =
    List(noToBeGenerated) { Random.nextDouble(fromNo, toNo) }

// number format
const val BYTES_TO_KB: Long = 1024
const val BYTES_TO_MB = BYTES_TO_KB * 1024
const val BYTES_TO_GB = BYTES_TO_MB * 1024
const val BYTES_TO_TB = BYTES_TO_GB * 1024

@SuppressLint("DefaultLocale")
fun Long.formatBytes(): String {
    if (this <= 0)
        return "0 bytes"
    return when {
        this / BYTES_TO_TB > 0 -> String.format("%.2f TB", this / BYTES_TO_TB.toFloat())
        this / BYTES_TO_GB > 0 -> String.format("%.2f GB", this / BYTES_TO_GB.toFloat())
        this / BYTES_TO_MB > 0 -> String.format("%.2f MB", this / BYTES_TO_MB.toFloat())
        this / BYTES_TO_KB > 0 -> String.format("%.2f KB", this / BYTES_TO_KB.toFloat())
        else -> "$this bytes"
    }
}

@SuppressLint("DefaultLocale")
fun Long.formatTime(context: Context): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return when {
        hours == 0L && minutes == 0L -> String.format("00:%02d", seconds)
        hours == 0L && minutes > 0L -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

@SuppressLint("DefaultLocale")
fun Long.formatAsTime(): String {
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(this) % 60L).toInt()
    val minutes = (TimeUnit.MILLISECONDS.toMinutes(this) % 60L).toInt()
    val hours = TimeUnit.MILLISECONDS.toHours(this).toInt()
    return if (hours == 0) String.format("%02d:%02d", minutes, seconds)
    else String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@SuppressLint("DefaultLocale")
fun Long.convertTimeToDisplayTime(): String {
    return String.format(
        "%d:%d",
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) -
                TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes(this)))
    )
}

fun formatCount(count: Int): String {
    val stringCount = count.toString()
    return when {
        count > 1_000_000_000 -> "${stringCount[0]}.${stringCount[1]}B"
        count > 100_000_000 -> "${stringCount.take(3)}.${stringCount[3]}M"
        count > 10_000_000 -> "${stringCount.take(2)}.${stringCount[2]}M"
        count > 1_000_000 -> "${stringCount[0]}.${stringCount[1]}M"
        count > 100_000 -> "${stringCount.take(3)}.${stringCount[3]}K"
        count > 10_000 -> "${stringCount.take(2)}.${stringCount[2]}K"
        count > 1_000 -> "${stringCount[0]}.${stringCount[1]}K"
        else -> stringCount
    }
}

// color
fun Int.toAlphaColor(alpha: Int): Int {
    return Color.argb(
        alpha,
        Color.red(this),
        Color.green(this),
        Color.blue(this)
    )
}

fun Float.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()
}