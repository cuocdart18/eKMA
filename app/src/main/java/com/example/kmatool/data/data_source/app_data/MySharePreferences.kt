package com.example.kmatool.data.data_source.app_data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class MySharePreferences(private val application: Application) {
    private val MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES"
    private val MY_ENCRYPTED_SHARED_PREFERENCES = "MY_ENCRYPTED_SHARED_PREFERENCES"

    private fun getEncryptedSharedPreferences(): SharedPreferences {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        return EncryptedSharedPreferences.create(
            MY_ENCRYPTED_SHARED_PREFERENCES,
            mainKeyAlias,
            application.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun putStringValueEncrypted(key: String, value: String) {
        getEncryptedSharedPreferences()
            .edit()
            .putString(key, value)
            .apply()
    }

    fun getStringValueEncrypted(key: String): String? {
        return getEncryptedSharedPreferences()
            .getString(key, "")
    }

    fun putBooleanValueEncrypted(key: String, value: Boolean) {
        getEncryptedSharedPreferences()
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getBooleanValueEncrypted(key: String): Boolean {
        return getEncryptedSharedPreferences()
            .getBoolean(key, false)
    }

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