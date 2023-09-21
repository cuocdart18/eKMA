package com.app.ekma.data.models

import com.app.ekma.R
import com.app.ekma.common.NOTE_TYPE
import com.app.ekma.common.toDateTime
import com.app.ekma.common.toLocalDate
import com.app.ekma.common.toLocalTime
import com.app.ekma.common.toMilli

data class Note(
    var title: String,
    var content: String? = null,
    var audioPath: String? = null,
    var date: String,
    var time: String
) : Event {
    var id: Int = 0
    var isDone: Boolean = false
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
    override fun getEventId() = id
}