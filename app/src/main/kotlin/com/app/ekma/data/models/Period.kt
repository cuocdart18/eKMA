package com.app.ekma.data.models

import com.app.ekma.R
import com.app.ekma.common.PERIOD_TYPE
import com.app.ekma.common.toDateTime
import com.app.ekma.common.toLocalDate
import com.app.ekma.common.toLocalTime
import com.app.ekma.common.toMilli

data class Period(
    val className: String,
    val day: String,
    val id: Int,
    val lesson: String,
    val room: String?,
    val subjectCode: String,
    val subjectName: String,
    val teacher: String
) : Event {
    var startTime: String = ""
    var endTime: String = ""
    override val type: Int = PERIOD_TYPE

    override fun getTimeCompare(): String = startTime

    override fun getTimeMillis() =
        toDateTime(day.toLocalDate(), startTime.toLocalTime()).toMilli()

    override fun getDateTime(): String = day

    override fun getContentTitleNotify(): String = subjectName
    override fun getSubTextNotify(): String = "Lịch học"
    override fun getContentTextNotify(): String = "$startTime | $room"
    override fun getContentBigTextNotify(): String = "$startTime\n$room"
    override fun getSmallIconNotify(): Int = R.drawable.school_outline_black_24dp
    override fun getLargeIconNotify(): Int = R.drawable.school
    override fun getEventId() = id
}