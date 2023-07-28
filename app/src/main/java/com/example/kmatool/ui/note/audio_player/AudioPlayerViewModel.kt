package com.example.kmatool.ui.note.audio_player

import com.example.kmatool.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor() : BaseViewModel() {
    override val TAG = AudioPlayerViewModel::class.java.simpleName
    var audioPath = ""
    private lateinit var voiceNotePlayer: VoiceNotePlayer

    fun initMediaPlayer(
        callback: (state: Int) -> Unit,
        callbackUpdateDuration: (duration: Int) -> Unit
    ) {
        if (!this::voiceNotePlayer.isInitialized) {
            voiceNotePlayer = VoiceNotePlayer(audioPath)
        }
        voiceNotePlayer.setCallback(callback, callbackUpdateDuration)
    }

    fun getAudioDuration(): Int {
        return voiceNotePlayer.getDuration()
    }

    fun getAudioCurrentDuration(): Int {
        return voiceNotePlayer.getCurrentDuration()
    }

    fun onClickBtnPlayPause() {
        when {
            voiceNotePlayer.isPaused -> voiceNotePlayer.resumePlayer()
            voiceNotePlayer.isPlaying -> voiceNotePlayer.pausePlayer()
            else -> voiceNotePlayer.startPlayer()
        }
    }

    fun seekToByUser(progress: Int) {
        voiceNotePlayer.seekTo(progress)
    }

    fun onPausePlayer() {
        if (voiceNotePlayer.isPlaying) {
            voiceNotePlayer.pausePlayer()
        }
    }

    private fun onReleaseMediaPlayer() {
        voiceNotePlayer.stopPlayer()
    }

    override fun onCleared() {
        super.onCleared()
        // release resource
        onReleaseMediaPlayer()
    }
}