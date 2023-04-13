package com.example.kmatool.data.local

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.kmatool.utils.KEY_IS_LOGIN
import com.example.kmatool.utils.KEY_STUDENT_PROFILE
import com.example.kmatool.utils.dataStore
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
}