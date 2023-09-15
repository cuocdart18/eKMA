package com.app.ekma.common

import java.io.File

object FileHelper {

    fun delete(path: String) {
        File(path).delete()
    }
}