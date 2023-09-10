package com.example.kmatool.common

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.min

suspend fun saveImageAndGetPath(context: Context, uri: Uri): String {
    return withContext(Dispatchers.IO) {
        val file = File(context.filesDir, AVATAR_FILE)

        try {
            val inputStream = context.contentResolver.openInputStream(uri)!!
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 50_000_000
            val bytesAvailable = inputStream.available()
            val bufferSize = min(bytesAvailable, maxBufferSize)
            val buffer = byteArrayOf()

            while ((inputStream.read(buffer).also { read = it }) != -1) {
                outputStream.write(buffer, 0, read)
            }

            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        file.absolutePath
    }
}

suspend fun copy(src: File, dst: File) {
    withContext(Dispatchers.IO) {
        try {
            val inputStream = FileInputStream(src)
            val outputStream = FileOutputStream(dst)
            // Transfer bytes from in to out
            val bytes = byteArrayOf()
            var length = 0
            while ((inputStream.read(bytes).also { length = it }) > 0) {
                outputStream.write(bytes, 0, length)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}