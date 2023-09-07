package com.example.kmatool.ui.note.main_scr

import android.content.Context
import android.media.MediaRecorder
import com.example.kmatool.common.PAUSE_RECORDING
import com.example.kmatool.common.RESUME_RECORDING
import com.example.kmatool.common.START_RECORDING
import com.example.kmatool.common.Timer
import com.example.kmatool.common.getCachedRecordDirPath
import com.example.kmatool.common.toMilli
import java.io.File
import java.time.LocalDateTime

class VoiceNoteRecorder {
    private var uiCallback: (state: Int) -> Unit = {}
    private var callbackUpdateDuration: (duration: Int) -> Unit = {}
    private lateinit var recorder: MediaRecorder
    private var timer: Timer = Timer(100) {
        currentDuration = it
        callbackUpdateDuration(it)
    }
    var outputCacheFile = ""
    var isRecording = false
    var isPaused = false
    private var currentDuration = 0

    fun setCallback(
        uiCallback: (state: Int) -> Unit,
        callbackUpdateDuration: (duration: Int) -> Unit
    ) {
        this.uiCallback = uiCallback
        this.callbackUpdateDuration = callbackUpdateDuration
    }

    fun getCurrentDuration() = currentDuration

    fun startRecorder(context: Context) {

        File(context.cacheDir, "records").mkdir()
        File(context.filesDir, "records").mkdir()
        outputCacheFile =
            "${getCachedRecordDirPath(context)}/record_${LocalDateTime.now().toMilli()}"
        recorder = MediaRecorder()
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputCacheFile)
            prepare()
            start()
        }
        timer.start()
        isRecording = true
        isPaused = false
        uiCallback(START_RECORDING)
    }

    fun pauseRecorder() {
        if (!isPaused) {
            recorder.pause()
            timer.pause()
            isPaused = true
            uiCallback(PAUSE_RECORDING)
        }
    }

    fun resumeRecorder() {
        if (isPaused) {
            recorder.resume()
            timer.start()
            isPaused = false
            uiCallback(RESUME_RECORDING)
        }
    }

    fun stopRecorder() {
        if (isRecording) {
            recorder.apply {
                stop()
                release()
            }
            timer.stop()
            currentDuration = 0
            isPaused = false
            isRecording = false
        }
    }
}