package com.example.kmatool.common

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import java.math.BigInteger
import java.security.MessageDigest

// Constants
const val KIT_URL = "https://www.facebook.com/kitclubKMA"
const val PERIOD_TYPE = 0
const val NOTE_TYPE = 1
const val ADD_NOTE_MODE = 101
const val UPDATE_NOTE_MODE = 102
const val EVENTS_NOTIFY_CHANNEL = "Events"
const val EVENTS_NOTIFY_CHANNEL_ID = "events_id"
const val KEY_EVENT = "sent_event_from_AM_to_BR"
const val EVENTS_NOTIFY_ID = 1001
const val TIRAMISU_PERMISSION_REQUEST_CODE = 33
const val S_PERMISSION_REQUEST_CODE = 31
const val M_PERMISSION_REQUEST_CODE = 23

// for database
const val KEY_PASS_MINISTUDENT_ID = "ministudent_id"
const val KEY_PASS_NOTE_OBJ = "note_obj"
const val KEY_PASS_NOTE_MODE = "note_mode"
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
const val KEY_IS_NOTIFY_EVENTS = "is_notify_events"
const val KEY_IMG_FILE_PATH = "img_file_path"

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