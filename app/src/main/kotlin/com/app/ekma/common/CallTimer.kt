package com.app.ekma.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

//  Usage: val timer = Timer(millisInFuture = 10_000L, runAtStart = false)
//  timer.start()
//  Ticker: https://stackoverflow.com/questions/54827455/how-to-implement-timer-with-kotlin-coroutines
enum class CallTimerMode {
    PLAYING,
    PAUSED,
    STOPPED
}

class CallTimer(
    private val millisStartPoint: Long = 0L,
    private val countInterval: Long = 1000L,
    runAtStart: Boolean = false,
    private val onEnd: ((Long) -> Unit) = {},
    private val onTick: ((Long) -> Unit) = {}
) {
    private var job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _tick = MutableStateFlow(0L)
    val tick = _tick.asStateFlow()

    private val _playerMode = MutableStateFlow(CallTimerMode.STOPPED)
    val playerMode = _playerMode.asStateFlow()

    init {
        if (runAtStart) start()
    }

    fun start() {
        if (_tick.value == 0L) _tick.value = millisStartPoint
        job.cancel()
        job = scope.launch(Dispatchers.IO) {
            _playerMode.value = CallTimerMode.PLAYING
            while (isActive) {
                delay(timeMillis = countInterval)
                _tick.value += countInterval
                onTick.invoke(_tick.value)
            }
        }
    }

    fun pause() {
        job.cancel()
        _playerMode.value = CallTimerMode.PAUSED
    }

    fun stop() {
        job.cancel()
        onEnd.invoke(_tick.value)
        _playerMode.value = CallTimerMode.STOPPED
    }
}