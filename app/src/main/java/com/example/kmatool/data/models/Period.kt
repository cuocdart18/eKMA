package com.example.kmatool.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.kmatool.R
import com.example.kmatool.common.toDateTime
import com.example.kmatool.common.toLocalDate
import com.example.kmatool.common.toLocalTime
import com.example.kmatool.common.toMilli
import com.example.kmatool.common.PERIOD_TYPE

@Entity(tableName = "period")
data class Period(
    val className: String,
    val day: String,
    @PrimaryKey
    val id: Int,
    val lesson: String,
    var startTime: String,
    var endTime: String,
    val room: String?,
    val subjectCode: String,
    val subjectName: String,
    val teacher: String
) : Event {
    @Ignore
    override val type: Int = PERIOD_TYPE

    override fun getTimeCompare(): String = startTime

    override fun getTimeMillis() =
        toDateTime(day.toLocalDate(), startTime.toLocalTime()).toMilli()

    override fun getDateTime(): String = day

    override fun getContentTitleNotify(): String = subjectName
    override fun getSubTextNotify(): String = "Lịch học"
    override fun getContentTextNotify(): String = "$startTime | $room | $teacher"
    override fun getContentBigTextNotify(): String = "$startTime\nPhòng $room\nGiảng viên $teacher"
    override fun getSmallIconNotify(): Int = R.drawable.ic_calendar_month_notify_24
}