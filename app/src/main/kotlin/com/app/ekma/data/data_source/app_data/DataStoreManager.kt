package com.app.ekma.data.data_source.app_data

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.ekma.common.KEY_IMG_FILE_PATH
import com.app.ekma.common.KEY_IS_LOGIN
import com.app.ekma.common.KEY_IS_NOTIFY_EVENTS
import com.app.ekma.common.KEY_STUDENT_PROFILE
import com.app.ekma.common.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map

class DataStoreManager(private val application: Application) {

    companion object {
        // profile
        private val PROFILE_DATA = stringPreferencesKey(KEY_STUDENT_PROFILE)

        // is login
        private val IS_LOGIN = booleanPreferencesKey(KEY_IS_LOGIN)

        // is notify
        private val IS_NOTIFY_EVENTS = booleanPreferencesKey(KEY_IS_NOTIFY_EVENTS)

        // uri path
        private val IMG_FILE_PATH = stringPreferencesKey(KEY_IMG_FILE_PATH)
    }

    val profileDataStoreFlow: Flow<String> = application.applicationContext.dataStore.data
        .cancellable()
        .map {
            it[PROFILE_DATA] ?: ""
        }

    suspend fun storeProfile(data: String) {
        application.applicationContext.dataStore.edit {
            it[PROFILE_DATA] = data
        }
    }

    val imgFilePathDataStoreFlow: Flow<String> = application.applicationContext.dataStore.data
        .cancellable()
        .map {
            it[IMG_FILE_PATH] ?: ""
        }

    suspend fun storeImgFilePath(data: String) {
        application.applicationContext.dataStore.edit {
            it[IMG_FILE_PATH] = data
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