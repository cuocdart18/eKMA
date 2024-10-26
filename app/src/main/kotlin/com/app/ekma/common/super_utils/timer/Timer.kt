package com.app.ekma.common.super_utils.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//  Usage: val timer = Timer(millisInFuture = 10_000L, runAtStart = false)
//  timer.start()
//  Ticker: https://stackoverflow.com/questions/54827455/how-to-implement-timer-with-kotlin-coroutines
enum class PlayerMode {
    PLAYING,
    PAUSED,
    STOPPED
}

class Timer(
    private val millisInFuture: Long,
    private val countDownInterval: Long = 1000L,
    runAtStart: Boolean = false,
    private val onFinish: (() -> Unit) = {},
    private val onTick: ((Long) -> Unit) = {}
) {
    private var job: Job = Job()
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _tick = MutableStateFlow(0L)
    val tick = _tick.asStateFlow()

    private val _playerMode = MutableStateFlow(PlayerMode.STOPPED)
    val playerMode = _playerMode.asStateFlow()

    init {
        if (runAtStart) start()
    }

    fun start() {
        if (_tick.value == 0L) _tick.value = millisInFuture
        job.cancel()
        job = scope.launch(Dispatchers.IO) {
            _playerMode.value = PlayerMode.PLAYING
            while (isActive) {
                if (_tick.value <= 0) {
                    job.cancel()
                    onFinish.invoke()
                    _playerMode.value = PlayerMode.STOPPED
                    return@launch
                }
                delay(timeMillis = countDownInterval)
                _tick.value -= countDownInterval
                onTick.invoke(this@Timer._tick.value)
            }
        }
    }

    fun pause() {
        job.cancel()
        _playerMode.value = PlayerMode.PAUSED
    }

    fun stop() {
        job.cancel()
        _tick.value = 0
        _playerMode.value = PlayerMode.STOPPED
    }
}