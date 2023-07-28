package com.example.kmatool.common

import android.os.Handler
import android.os.Looper

class Timer(
    private val delay: Int,
    private val callback: (duration: Int) -> Unit
) {
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var duration = 0

    init {
        runnable = Runnable {
            duration += delay
            callback(duration)
            handler.postDelayed(runnable, delay.toLong())
        }
    }

    fun start() {
        handler.postDelayed(runnable, 0)
    }

    fun pause() {
        handler.removeCallbacks(runnable)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0
    }
}