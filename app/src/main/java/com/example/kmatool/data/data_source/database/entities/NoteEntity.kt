package com.example.kmatool.data.data_source.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NoteEntityEntry.TBL_NAME)
data class NoteEntity(
    @ColumnInfo(name = NoteEntityEntry.TITLE)
    var title: String,
    @ColumnInfo(name = NoteEntityEntry.CONTENT)
    var content: String? = null,
    @ColumnInfo(name = NoteEntityEntry.DATE)
    var date: String,
    @ColumnInfo(name = NoteEntityEntry.TIME)
    var time: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = NoteEntityEntry.ID)
    var id: Int = 0
}

object NoteEntityEntry {
    const val TBL_NAME = "tbl_note"
    const val ID = "id"
    const val TITLE = "title"
    const val CONTENT = "content"
    const val DATE = "date"
    const val TIME = "time"
}