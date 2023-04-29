package com.example.kmatool.data.models

import java.io.Serializable

interface Event : Serializable {
    val type: Int

    fun getTimeCompare(): String

    fun getTimeMillis(): Long

    fun getDateTime(): String

    // to notify
    fun getContentTitleNotify(): String
    fun getSubTextNotify(): String
    fun getContentTextNotify(): String
    fun getContentBigTextNotify(): String
    fun getSmallIconNotify(): Int
}