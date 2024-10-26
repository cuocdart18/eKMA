package com.app.ekma.common.super_utils.number_string_date

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale

fun String.removeAllSpaces(isLowerCase: Boolean): String {
    return if (isNullOrEmpty()) {
        ""
    } else if (isLowerCase) {
        trim { it <= ' ' }.replace(" ".toRegex(), "").lowercase(Locale.getDefault())
    } else {
        trim { it <= ' ' }.replace(" ".toRegex(), "").uppercase(Locale.getDefault())
    }
}

fun String.copyTextToClipboard(activity: Activity) {
    val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", this)
    clipboardManager.setPrimaryClip(clipData)
}

fun String.convertStringToURL(): URL? {
    try {
        return URL(this)
    } catch (e: MalformedURLException) {
        e.printStackTrace()
    }
    return null
}

