package com.app.ekma.ui.note.audio_player

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.app.ekma.base.viewmodel.BaseViewModel
import com.app.ekma.common.APP_EXTERNAL_MEDIA_FOLDER
import com.app.ekma.common.EXTERNAL_AUDIO_FOLDER
import com.app.ekma.data.models.service.IProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val profileService: IProfileService
) : BaseViewModel() {
    override val TAG = AudioPlayerViewModel::class.java.simpleName
    var audioName = ""
    private lateinit var voiceNotePlayer: VoiceNotePlayer

    suspend fun initMediaPlayer(
        context: Context,
        callback: (state: Int) -> Unit,
        callbackUpdateDuration: (duration: Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (!this@AudioPlayerViewModel::voiceNotePlayer.isInitialized) {
            val myStudentCode = profileService.getProfile().studentCode
            val audioPath = File(
                context.getExternalFilesDir("$APP_EXTERNAL_MEDIA_FOLDER/$myStudentCode/$EXTERNAL_AUDIO_FOLDER"),
                audioName
            ).absolutePath
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