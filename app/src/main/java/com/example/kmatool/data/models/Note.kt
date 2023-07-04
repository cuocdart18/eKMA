package com.example.kmatool.data.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.kmatool.R
import com.example.kmatool.common.toDateTime
import com.example.kmatool.common.toLocalDate
import com.example.kmatool.common.toLocalTime
import com.example.kmatool.common.toMilli
import com.example.kmatool.common.NOTE_TYPE

data class Note(
    var title: String,
    var content: String? = null,
    var date: String,
    var time: String
) : Event {
    var id: Int = 0

    override val type: Int = NOTE_TYPE

    override fun getTimeCompare(): String = time

    override fun getTimeMillis() =
        toDateTime(date.toLocalDate(), time.toLocalTime()).toMilli()

    override fun getDateTime(): String = date

    override fun getContentTitleNotify(): String = title
    override fun getSubTextNotify(): String = "Ghi chú"
    override fun getContentTextNotify(): String = content.toString()
    override fun getContentBigTextNotify(): String = content.toString()
    override fun getSmallIconNotify(): Int = R.drawable.school_outline_black_24dp
    override fun getLargeIconNotify(): Int = R.drawable.notes
}