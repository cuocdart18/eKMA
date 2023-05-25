package com.example.kmatool.data.data_source.apis.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MiniStudentDto(
    var id: String,
    var name: String,
    @SerializedName("class")
    var classInSchool: String
)