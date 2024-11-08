package com.app.ekma.common.super_utils.click

import android.os.SystemClock
import android.view.View

class OnSingleClickListener(private val block: (View) -> Unit) : View.OnClickListener {

    private var lastClickTime = 0L

    override fun onClick(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        block(view)
    }
}

fun View.setOnSingleClickListener(block: (View) -> Unit) {
    val onClick = OnSingleClickListener {
        block(it)
    }
    setOnClickListener(onClick)
}