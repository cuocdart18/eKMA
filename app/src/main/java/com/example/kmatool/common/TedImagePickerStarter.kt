package com.example.kmatool.common

import android.content.Context
import android.net.Uri
import android.util.Log
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType

object TedImagePickerStarter {

    fun startImage(
        context: Context,
        callback: (uri: Uri) -> Unit
    ) {
        TedImagePicker.with(context)
            .mediaType(MediaType.IMAGE)
            .title("Select an image")
            .start { uri ->
                Log.d("TedImagePickerStarter", "get uri=$uri")
                callback(uri)
            }
    }
}