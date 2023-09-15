package com.app.ekma.data.models

import java.io.Serializable

interface Event : Serializable {
    val type: Int

    fun getEventId(): Int

    fun getTimeCompare(): String

    fun getTimeMillis(): Long

    fun getDateTime(): String

    // to notify
    fun getContentTitleNotify(): String
    fun getSubTextNotify(): String
    fun getContentTextNotify(): String
    fun getContentBigTextNotify(): String
    fun getSmallIconNotify(): Int
    fun getLargeIconNotify(): Int
}