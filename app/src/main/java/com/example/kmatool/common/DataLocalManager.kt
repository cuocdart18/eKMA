package com.example.kmatool.common

import javax.inject.Singleton

@Singleton
class DataLocalManager (private val mySharePreferences: MySharePreferences) {
    val LOGIN_STATE = "login_state"

    fun setLoginState(isLogin: Boolean) {
        mySharePreferences.putBooleanValue(LOGIN_STATE, isLogin)
    }

    fun getLoginState(): Boolean {
        return mySharePreferences.getBooleanValue(LOGIN_STATE)
    }
}