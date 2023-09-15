package com.app.ekma.common

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
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
            val buffer = ByteArray(bufferSize)

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

@Throws(IOException::class)
fun copy(src: File?, dst: File?) {
    FileInputStream(src).use { `in` ->
        FileOutputStream(dst).use { out ->
            // Transfer bytes from in to out
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
        }
    }
}

suspend fun copy(src: File, dst: File) {
    withContext(Dispatchers.IO) {
        try {
            val inputStream = FileInputStream(src)
            val outputStream = FileOutputStream(dst)
            // Transfer bytes from in to out
            val bytes = ByteArray(1024)
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