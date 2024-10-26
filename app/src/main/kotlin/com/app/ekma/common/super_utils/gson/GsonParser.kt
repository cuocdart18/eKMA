package com.app.ekma.common.super_utils.gson

import com.google.gson.Gson

fun jsonObjectToString(data: Any): String {
    val gson = Gson()
    return gson.toJson(data)
}

inline fun <reified T> jsonStringToObject(data: String): T {
    val gson = Gson()
    return gson.fromJson(data, T::class.java)
}

fun <T> stringToArray(s: String, clazz: Class<Array<T>>): List<T> {
    val arr = Gson().fromJson(s, clazz)
    return listOf(*arr)
}