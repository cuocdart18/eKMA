package com.app.ekma.ui.note.audio_player

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.APP_EXTERNAL_MEDIA_FOLDER
import com.app.ekma.common.EXTERNAL_AUDIO_FOLDER
import com.app.ekma.common.ProfileSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor() : BaseViewModel() {
    override val TAG = AudioPlayerViewModel::class.java.simpleName
    private lateinit var myStudentCode: String
    var audioName = ""
    private lateinit var audioFile: File
    private lateinit var voiceNotePlayer: VoiceNotePlayer

    fun checkAudioFileExists(
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        if (this::audioFile.isInitialized) {
            callback(audioFile.exists())
        } else {
            viewModelScope.launch {
                myStudentCode = ProfileSingleton().studentCode
                audioFile = File(
                    context.getExternalFilesDir("$APP_EXTERNAL_MEDIA_FOLDER/$myStudentCode/$EXTERNAL_AUDIO_FOLDER"),
                    audioName
                )
                callback(audioFile.exists())
            }
        }
    }

    fun initMediaPlayer(
        callbackState: (state: Int) -> Unit,
        callbackUpdateDuration: (duration: Int) -> Unit
    ) {
        if (!this::voiceNotePlayer.isInitialized) {
            voiceNotePlayer = VoiceNotePlayer(audioFile.absolutePath)
        }
        voiceNotePlayer.setCallback(callbackState, callbackUpdateDuration)
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
        if (this::voiceNotePlayer.isInitialized) {
            if (voiceNotePlayer.isPlaying) {
                voiceNotePlayer.pausePlayer()
            }
        }
    }

    private fun onReleaseMediaPlayer() {
        if (this::voiceNotePlayer.isInitialized) {
            voiceNotePlayer.stopPlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // release resource
        onReleaseMediaPlayer()
    }
}