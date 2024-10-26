package com.app.ekma.common.super_utils.app

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.app.ekma.BuildConfig
import java.io.File

fun Context.restartApp() {
    val restartIntent = packageManager.getLaunchIntentForPackage(packageName)!!
    restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(restartIntent)
}

fun Context.shareApp() {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    val shareBody = "https://play.google.com/store/apps/details?id=$packageName"
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "")
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
    startActivity(
        Intent.createChooser(sharingIntent, "Share to").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun Context.support(email: String, sub: String) {
    val mailIntent = Intent(Intent.ACTION_VIEW)
    val data = Uri.parse("mailto:?SUBJECT=$sub&body=&to=$email")
    mailIntent.data = data
    startActivity(
        Intent.createChooser(mailIntent, "Send mail...").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun Context.rateApp() {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (anfe: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

fun Context.openStore(pkg: String?) {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
            )
        )
    } catch (anfe: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
            )
        )
    }
}

fun Context.openDetailStore(packageName: String) {
    runCatching {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

fun Context.showPolicy(policyUrl: String) {
    openWeb(policyUrl)
}

fun Context.openWeb(url: String?) {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.gotoSettingDialog() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Permission Denied!")
    builder.setMessage("Go to setting to accept permission!")
    builder.setPositiveButton(
        "Go Setting"
    ) { _, _ ->
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:$packageName")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(i)
    }
    val dialog = builder.create()
    dialog.show()
}

fun Context.shareFile(file: File?, fileName: String?, pkg: String? = null) {
    try {
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file!!)
        shareFile(this, uri, fileName, pkg)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

private fun shareFile(context: Context, uri: Uri?, fileName: String?, pkg: String?) {
    val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
    try {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, fileName)
        emailIntent.`package` = pkg
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.setDataAndType(
            uri, context.contentResolver.getType(
                uri!!
            )
        )
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(emailIntent)
    } catch (ex: Exception) {
        try {
            val intent: Intent = ShareCompat.IntentBuilder.from(context as Activity).setType(
                context.contentResolver.getType(
                    uri!!
                )
            ).setStream(uri).intent
            intent.putExtra(Intent.EXTRA_EMAIL, "")
            intent.putExtra(Intent.EXTRA_SUBJECT, fileName)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setPackage(pkg)
            val createChooser: Intent = Intent.createChooser(intent, "Share File")
            createChooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (createChooser.resolveActivity(context.packageManager) == null) {
                return
            }
            context.startActivity(createChooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}