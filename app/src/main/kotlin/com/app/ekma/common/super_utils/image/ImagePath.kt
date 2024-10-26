package com.app.ekma.common.super_utils.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

fun getBase64FromImageSystem(imagePath: String): String {
    // Read the image from file
    val imageFile = File(imagePath)
    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}