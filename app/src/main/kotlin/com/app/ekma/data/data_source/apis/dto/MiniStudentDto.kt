package com.app.ekma.data.data_source.apis.dto

import com.google.gson.annotations.SerializedName

data class MiniStudentDto(
    var id: String,
    var name: String,
    @SerializedName("class")
    var classInSchool: String
)