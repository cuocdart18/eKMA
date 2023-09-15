package com.app.ekma.data.models

import java.util.Date

data class MiniStudent(
    var id: String,
    var name: String,
    var classInSchool: String,
    var dateModified: Date
) {
    var index: Int = 0
}
