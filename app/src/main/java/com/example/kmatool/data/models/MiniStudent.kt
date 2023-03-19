package com.example.kmatool.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.kmatool.common.Converters
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "mini_student")
data class MiniStudent(
    @PrimaryKey
    var id: String,
    var name: String,
    @SerializedName("class")
    var classInSchool: String,
    @TypeConverters(Converters::class)
    var dateModified: Date
) {
    @Ignore
    var index: Int = 0
}
