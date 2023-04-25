package com.example.kmatool.common

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(private val application: Application) {

    companion object {
        // profile
        private val PROFILE_DATA = stringPreferencesKey(KEY_STUDENT_PROFILE)

        // is login
        private val IS_LOGIN = booleanPreferencesKey(KEY_IS_LOGIN)

        // is notify
        private val IS_NOTIFY_EVENTS = booleanPreferencesKey(KEY_IS_NOTIFY_EVENTS)
    }

    val profileDataStoreFlow: Flow<String> = application.applicationContext.dataStore.data
        .map {
            it[PROFILE_DATA] ?: ""
        }

    suspend fun storeProfile(data: String) {
        application.applicationContext.dataStore.edit {
            it[PROFILE_DATA] = data
        }
    }

    val isLoginDataStoreFlow: Flow<Boolean> = application.applicationContext.dataStore.data
        .cancellable()
        .map {
            it[IS_LOGIN] ?: false
        }

    suspend fun storeIsLogin(data: Boolean) {
        application.applicationContext.dataStore.edit {
            it[IS_LOGIN] = data
        }
    }

    val isNotifyEventsDataStoreFlow: Flow<Boolean> = application.applicationContext.dataStore.data
        .cancellable()
        .map {
            it[IS_NOTIFY_EVENTS] ?: false
        }

    suspend fun storeIsNotifyEvents(data: Boolean) {
        application.applicationContext.dataStore.edit {
            it[IS_NOTIFY_EVENTS] = data
        }
    }
}