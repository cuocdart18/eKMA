package com.example.kmatool.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.kmatool.common.Converters
import java.util.Date

@Entity(tableName = MiniStudentEntityEntry.TBL_NAME)
data class MiniStudentEntity(
    @PrimaryKey
    @ColumnInfo(name = MiniStudentEntityEntry.ID)
    var id: String,
    @ColumnInfo(name = MiniStudentEntityEntry.NAME)
    var name: String,
    @ColumnInfo(name = MiniStudentEntityEntry.CLASS)
    var classInSchool: String,
    @TypeConverters(Converters::class)
    @ColumnInfo(name = MiniStudentEntityEntry.DATE_MODIFIED)
    var dateModified: Date
)

object MiniStudentEntityEntry {
    const val TBL_NAME = "tbl_mini_student"
    const val ID = "id"
    const val NAME = "name"
    const val CLASS = "class"
    const val DATE_MODIFIED = "date_modified"
}