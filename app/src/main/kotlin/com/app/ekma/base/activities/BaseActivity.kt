package com.app.ekma.base.activities

import android.app.PictureInPictureUiState
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.ekma.R
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable

open class BaseActivity : AppCompatActivity() {
    protected open val TAG = ""

    // LIFECYCLE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        logLifecycle("onUserInteraction")
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        logLifecycle("onUserLeaveHint")
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
            logLifecycle("onPictureInPictureModeChanged")
        }
    }

    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        super.onPictureInPictureUiStateChanged(pipState)
        logLifecycle("onPictureInPictureUiStateChanged")
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy")
    }

    // LOG
    open fun logLifecycle(msg: String) {
        Log.d(TAG, msg)
    }

    open fun logError(msg: String) {
        Log.e(TAG, msg)
    }

    open fun logDebug(msg: String) {
        Log.d(TAG, msg)
    }

    open fun logInfo(msg: String) {
        Log.i(TAG, msg)
    }

    open fun logWarning(msg: String) {
        Log.w(TAG, msg)
    }

    // METHOD
    internal fun <T> openActivityWithFinish(cls: Class<T>) {
        val intent = Intent(this, cls)
        startActivity(intent)
        finish()
    }

    internal fun <T> openActivityNoFinish(cls: Class<T>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    internal fun showToast(msg: String, type: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, type).show()
    }

    internal fun setupGoogleProgress(progressBar: ProgressBar) {
        progressBar.indeterminateDrawable =
            ChromeFloatingCirclesDrawable.Builder(this)
                .colors(resources.getIntArray(R.array.google_colors))
                .build()
    }
}