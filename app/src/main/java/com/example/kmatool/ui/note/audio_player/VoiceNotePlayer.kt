package com.example.kmatool.ui.note.audio_player

import android.media.MediaPlayer
import com.example.kmatool.common.PAUSE_PLAYING
import com.example.kmatool.common.RESUME_PLAYING
import com.example.kmatool.common.START_PLAYING
import com.example.kmatool.common.STOP_PLAYING
import com.example.kmatool.common.Timer

class VoiceNotePlayer(
    private val inputFile: String
) {
    private var uiCallback: (state: Int) -> Unit = {}
    private var callbackUpdateDuration: (duration: Int) -> Unit = {}
    private var mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setDataSource(inputFile)
        prepare()
    }
    private var timer: Timer = Timer(50) { callbackUpdateDuration(mediaPlayer.currentPosition) }
    var isPlaying = false
    var isPaused = false

    init {
        mediaPlayer.setOnCompletionListener { onAudioCompletion() }
    }

    fun setCallback(
        callback: (state: Int) -> Unit,
        callbackUpdateDuration: (duration: Int) -> Unit
    ) {
        this.uiCallback = callback
        this.callbackUpdateDuration = callbackUpdateDuration
    }

    fun startPlayer() {
        if (!isPlaying) {
            mediaPlayer.start()
            timer.start()
            isPlaying = true
            isPaused = false
            uiCallback(START_PLAYING)
        }
    }

    fun seekTo(progress: Int) {
        mediaPlayer.seekTo(progress)
    }

    fun getCurrentDuration(): Int {
        return mediaPlayer.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun pausePlayer() {
        if (!isPaused) {
            mediaPlayer.pause()
            timer.pause()
            isPaused = true
            uiCallback(PAUSE_PLAYING)
        }
    }

    fun resumePlayer() {
        if (isPaused) {
            mediaPlayer.start()
            timer.start()
            isPaused = false
            uiCallback(RESUME_PLAYING)
        }
    }

    fun stopPlayer() {
        if (isPlaying) {
            mediaPlayer.apply {
                stop()
                release()
            }
            timer.stop()
            isPlaying = false
            isPaused = false
            uiCallback(STOP_PLAYING)
        }
    }

    private fun onAudioCompletion() {
        timer.stop()
        mediaPlayer.pause()
        timer.pause()
        isPaused = true
        uiCallback(PAUSE_PLAYING)
    }
}