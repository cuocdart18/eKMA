package com.app.ekma.common.super_utils.ffmpeg

import Jni.VideoUitls
import VideoHandle.CmdList
import VideoHandle.EpEditor
import VideoHandle.OnEditorListener
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.app.ekma.R
import com.app.ekma.common.super_utils.number_string_date.dpToPx
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun muxing(
    audioPath: String,
    inputVideoPath: String,
    outputVideoPath: String,
    onFinish: (isSuccess: Boolean) -> Unit
) {
    val TAG = "Muxing"
    try {
        EpEditor.music(inputVideoPath,
            audioPath,
            outputVideoPath,
            1f,
            0.5f,
            object : OnEditorListener {
                override fun onSuccess() {
                    Log.d(TAG, "onSuccess: ")
                    onFinish(true)
                }

                override fun onFailure() {
                    Log.d(TAG, "onFailure: ")
                    onFinish(false)
                }

                override fun onProgress(progress: Float) {
                    Log.d(TAG, "onProgress: progress = $progress")
                }

            })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Bitmap.toFile(): File {
    val file = File.createTempFile("image_" + System.currentTimeMillis(), ".png")
    val bytes = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, bytes)
    try {
        val fo = FileOutputStream(file)
        fo.write(bytes.toByteArray())
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

fun watermark(
    context: Context,
    input: String,
    output: String,
    x: Int,
    y: Int,
    onFinish: (isSuccess: Boolean) -> Unit
) {
    val watermark by lazy {
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.id_card_outline_black_16dp
            ), 100F.dpToPx(context), 100F.dpToPx(context), false
        )
    }
    val watermarkFile = watermark.toFile()

    val cmd = CmdList()
    cmd.append("-i").append(input)
        .append("-i")
        .append(watermarkFile.absolutePath)
        .append("-filter_complex")
        .append("[0:v][1:v]overlay=main_w-overlay_w-$x:main_h-overlay_h-$y")
        .append("-preset").append("ultrafast")
        .append("-c:v").append("libx264")
        .append("-c:a").append("aac")
        .append("-max_muxing_queue_size 9999")
        .append(output)
    val duration = VideoUitls.getDuration(input)
    EpEditor.execCmd(cmd.toString().trim().apply { }, duration, object : OnEditorListener {
        override fun onSuccess() {
            Log.d("TAG", "onWatermarkSuccess: -----------------------")
            onFinish(true)
        }

        override fun onFailure() {
            Log.d("TAG", "onWatermarkFailure: ------------------------")
            onFinish(false)
        }

        override fun onProgress(progress: Float) {
            Log.d("TAG", "onWatermarkProgress: --------------------$progress")
        }
    })
}