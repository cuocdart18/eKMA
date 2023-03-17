package com.example.kmatool.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.math.BigInteger
import java.security.MessageDigest

// Constants
const val KIT_URL = "https://www.facebook.com/kitclubKMA"

// for database
const val KEY_PASS_MINISTUDENT_ID = "ministudent_id"
const val DATABASE_NAME = "app_database.db"
const val KEY_PASS_STATISTIC_SUBJECT = "statistic_subject"
const val SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_X = 0.80
const val SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_Y = 0.25
const val SCALE_LAYOUT_SEARCH_DATA_DIALOG_X = 0.80
const val SCALE_LAYOUT_SEARCH_DATA_DIALOG_Y = 0.60

// for data store
const val NAME_DATASTORE_PREFS = "datastore_prefs"
const val KEY_STUDENT_PROFILE = "student_profile"
const val KEY_IS_LOGIN = "is_login"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = NAME_DATASTORE_PREFS)

// Global method

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