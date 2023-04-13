package com.example.kmatool.data.local

import android.app.Application
import android.content.Context
import javax.inject.Inject

class MySharePreferences @Inject constructor(private val application: Application) {
    private val MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES"

    fun putIntValue(key: String, value: Int) {
        application.applicationContext
            .getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putInt(key, value)
            .apply()
    }

    fun getIntValue(key: String): Int {
        return application.applicationContext
            .getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .getInt(key, 0)
    }

    fun putStringValue(key: String, value: String) {
        application.applicationContext
            .getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }

    fun getStringValue(key: String): String? {
        return application.applicationContext
            .getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .getString(key, "")
    }

    fun putBooleanValue(key: String, value: Boolean) {
        application.applicationContext
            .getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getBooleanValue(key: String): Boolean {
        return application.applicationContext
            .getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }
}