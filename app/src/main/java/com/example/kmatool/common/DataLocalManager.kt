package com.example.kmatool.common

import android.util.Log
import javax.inject.Singleton

@Singleton
class DataLocalManager(
    private val mySharePreferences: MySharePreferences,
    private val dataStoreManager: DataStoreManager
) {
    private val TAG = DataLocalManager::class.java.simpleName
    private val KEY_LOGIN_STATE = "login_state_sPref"
    private val KEY_IMG_PATH = "img_path_sPref"
    private val KEY_PROFILE = "profile_sPref"
    private val KEY_NOTIFY_EVENTS = "notify_events_sPref"
    private val KEY_USERNAME = "username_sPref"
    private val KEY_PASSWORD = "password_sPref"

    suspend fun savePassword(data: String) {
        Log.d(TAG, "save password = $data")
        mySharePreferences.putStringValue(KEY_PASSWORD, data)
    }

    suspend fun getPassword(): String {
        val data = mySharePreferences.getStringValue(KEY_PASSWORD).toString()
        Log.d(TAG, "get password = $data")
        return data
    }

    suspend fun saveUsername(data: String) {
        Log.d(TAG, "save username = $data")
        mySharePreferences.putStringValue(KEY_USERNAME, data)
    }

    suspend fun getUsername(): String {
        val data = mySharePreferences.getStringValue(KEY_USERNAME).toString()
        Log.d(TAG, "get username = $data")
        return data
    }

    suspend fun saveImgFilePathSPref(data: String) {
        Log.d(TAG, "save image file path = $data")
        mySharePreferences.putStringValue(KEY_IMG_PATH, data)
    }

    suspend fun getImgFilePathSPref(): String {
        val data = mySharePreferences.getStringValue(KEY_IMG_PATH).toString()
        Log.d(TAG, "get image file path = $data")
        return data
    }

    suspend fun saveLoginStateSPref(isLogin: Boolean) {
        Log.d(TAG, "save login state = $isLogin")
        mySharePreferences.putBooleanValue(KEY_LOGIN_STATE, isLogin)
    }

    suspend fun getLoginStateSPref(): Boolean {
        val data = mySharePreferences.getBooleanValue(KEY_LOGIN_STATE)
        Log.d(TAG, "get login state = $data")
        return data
    }

    suspend fun saveProfileSPref(data: String) {
        Log.d(TAG, "save profile = $data")
        mySharePreferences.putStringValue(KEY_PROFILE, data)
    }

    suspend fun getProfileSPref(): String {
        val data = mySharePreferences.getStringValue(KEY_PROFILE).toString()
        Log.d(TAG, "get profile = $data")
        return data
    }

    suspend fun saveIsNotifyEventsSPref(data: Boolean) {
        Log.d(TAG, "save notify event = $data")
        mySharePreferences.putBooleanValue(KEY_NOTIFY_EVENTS, data)
    }

    suspend fun getIsNotifyEventsSPref(): Boolean {
        val data = mySharePreferences.getBooleanValue(KEY_NOTIFY_EVENTS)
        Log.d(TAG, "get notify event = $data")
        return data
    }

    suspend fun saveProfile(data: String) {
        Log.d(TAG, "save profile = $data")
        dataStoreManager.storeProfile(data)
    }

    suspend fun getProfile(
        callback: (data: String) -> Unit
    ) {
        dataStoreManager.profileDataStoreFlow.collect() {
            Log.d(TAG, "get profile = $it")
            callback(it)
        }
    }

    suspend fun saveImgFilePath(data: String) {
        dataStoreManager.storeImgFilePath(data)
    }

    suspend fun getImgFilePath(
        callback: (data: String) -> Unit
    ) {
        dataStoreManager.imgFilePathDataStoreFlow.collect() {
            callback(it)
        }
    }

    suspend fun saveIsLogin(data: Boolean) {
        dataStoreManager.storeIsLogin(data)
    }

    suspend fun getIsLogin(
        callback: (data: Boolean) -> Unit
    ) {
        dataStoreManager.isLoginDataStoreFlow.collect() {
            callback(it)
        }
    }

    suspend fun saveIsNotifyEvents(data: Boolean) {
        dataStoreManager.storeIsNotifyEvents(data)
        Log.d(TAG, "save notify event = $data")
    }

    suspend fun getIsNotifyEvents(
        callback: (data: Boolean) -> Unit
    ) {
        dataStoreManager.isNotifyEventsDataStoreFlow.collect() {
            Log.d(TAG, "get notify event = $it")
            callback(it)
        }
    }
}