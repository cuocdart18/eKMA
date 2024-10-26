package com.app.ekma.common.super_utils.click

import android.annotation.SuppressLint
import android.os.Handler
import android.view.View

private var mLastClickTime: Long = 0L

@SuppressLint("StaticFieldLeak")
private var currentViewClick: View? = null
fun View.performClick(timeDelay: Long = 1000, onClick: () -> Unit) {
    if (this != currentViewClick) {
        mLastClickTime = 0
    }
    currentViewClick = this
    this.setOnClickListener {
        this.isEnabled = false
        onClick.invoke()
        Handler().postDelayed({
            this.isEnabled = true
        }, timeDelay)
    }
}