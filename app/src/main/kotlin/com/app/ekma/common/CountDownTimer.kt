package com.app.ekma.common

import android.os.Handler
import android.os.Looper

class CountDownTimer(
    var count: Long,
    private val finish: () -> Unit
) {

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val delay = 1000L

    init {
        runnable = Runnable {
            count -= delay
            if (count == 0L) {
                finish()
            } else {
                handler.postDelayed(runnable, delay)
            }
        }
    }

    fun start() {
        handler.postDelayed(runnable, 0)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        count = 0
    }
}