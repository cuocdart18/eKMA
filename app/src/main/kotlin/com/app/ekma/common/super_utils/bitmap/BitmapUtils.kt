package com.app.ekma.common.super_utils.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.app.ekma.common.super_utils.number_string_date.convertStringToURL
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun getBitmapFromUrl(string: String): Bitmap? {
    val connection: HttpURLConnection?
    try {
        val url: URL = string.convertStringToURL()!!
        connection = url.openConnection() as HttpURLConnection
        connection.connect()
        val inputStream: InputStream = connection.inputStream
        val bufferedInputStream = BufferedInputStream(inputStream)
        return BitmapFactory.decodeStream(bufferedInputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

suspend fun getBitmapFromUrl(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            Glide.with(context)
                .asBitmap()
                .load(uri)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun getBitmapFromBase64(base64String: String): Bitmap? {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    val inputStream = ByteArrayInputStream(decodedBytes)
    return BitmapFactory.decodeStream(inputStream)
}