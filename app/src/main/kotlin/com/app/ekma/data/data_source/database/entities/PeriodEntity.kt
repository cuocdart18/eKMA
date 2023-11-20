package com.app.ekma.data.data_source.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PeriodEntityEntry.TBL_NAME)
data class PeriodEntity(
    @ColumnInfo(name = PeriodEntityEntry.CLASS_NAME)
    val className: String,
    @ColumnInfo(name = PeriodEntityEntry.DAY)
    val day: String,
    @PrimaryKey
    @ColumnInfo(name = PeriodEntityEntry.ID)
    val id: Int,
    @ColumnInfo(name = PeriodEntityEntry.LESSON)
    val lesson: String,
    @ColumnInfo(name = PeriodEntityEntry.ROOM)
    val room: String?,
    @ColumnInfo(name = PeriodEntityEntry.SUBJECT_CODE)
    val subjectCode: String,
    @ColumnInfo(name = PeriodEntityEntry.SUBJECT_NAME)
    val subjectName: String,
    @ColumnInfo(name = PeriodEntityEntry.TEACHER)
    val teacher: String
)

object PeriodEntityEntry {
    const val TBL_NAME = "tbl_period"
    const val CLASS_NAME = "className"
    const val DAY = "day"
    const val ID = "id"
    const val LESSON = "lesson"
    const val ROOM = "room"
    const val SUBJECT_CODE = "subject_code"
    const val SUBJECT_NAME = "subject_name"
    const val TEACHER = "teacher"
}