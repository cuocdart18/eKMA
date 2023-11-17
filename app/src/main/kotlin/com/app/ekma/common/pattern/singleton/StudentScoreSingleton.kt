package com.app.ekma.common.pattern.singleton

import com.app.ekma.data.models.Student

object StudentScoreSingleton {
    private var student: Student? = null

    operator fun invoke() = student

    fun setData(student: Student) = synchronized(this) {
        StudentScoreSingleton.student = student
    }

    fun release() {
        student = null
    }
}