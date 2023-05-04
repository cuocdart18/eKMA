package com.example.kmatool.common

import javax.inject.Singleton

@Singleton
class DataLocalManager(
    private val mySharePreferences: MySharePreferences,
    private val dataStoreManager: DataStoreManager
) {
    private val LOGIN_STATE = "login_state"

    suspend fun saveLoginState(isLogin: Boolean) {
        mySharePreferences.putBooleanValue(LOGIN_STATE, isLogin)
    }

    suspend fun getLoginState(): Boolean {
        return mySharePreferences.getBooleanValue(LOGIN_STATE)
    }

    suspend fun saveProfile(data: String) {
        dataStoreManager.storeProfile(data)
    }

    suspend fun getProfile(
        callback: (data: String) -> Unit
    ) {
        dataStoreManager.profileDataStoreFlow.collect() {
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
    }

    suspend fun getIsNotifyEvents(
        callback: (data: Boolean) -> Unit
    ) {
        dataStoreManager.isNotifyEventsDataStoreFlow.collect() {
            callback(it)
        }
    }
}