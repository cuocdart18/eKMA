package com.app.ekma.common

import com.app.ekma.data.models.Student

object StudentScoreSingleton {
    private var student: Student? = null

    operator fun invoke() = student

    fun setData(student: Student) = synchronized(this) {
        this.student = student
    }

    fun release() {
        this.student = null
    }
}