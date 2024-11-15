package com.app.ekma.data.data_source.app_data

interface IDataLocalManager {

    suspend fun saveUser(data: String)

    suspend fun getUser(): String

    suspend fun savePassword(data: String)

    suspend fun getPassword(): String

    suspend fun saveUsername(data: String)

    suspend fun getUsername(): String

    suspend fun saveImgFilePath(data: String)

    suspend fun getImgFilePath(): String

    suspend fun saveLoginState(isLogin: Boolean)

    suspend fun getLoginState(): Boolean

    suspend fun saveProfile(data: String)

    suspend fun getProfile(): String

    suspend fun saveProfileDetail(data: String)

    suspend fun getProfileDetail(): String

    suspend fun saveIsNotifyEvents(data: Boolean)

    suspend fun getIsNotifyEvents(): Boolean
}