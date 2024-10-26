package com.app.ekma.common.super_utils.app

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

object AppSoundPlayerManager {
    private var mediaPlayer: MediaPlayer? = null

    fun initialize(context: Context, rawId: Int, isAutoPlay: Boolean) = runCatching {
        release()

        mediaPlayer = MediaPlayer.create(context, rawId).apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            isLooping = true

            runCatching {
//                setDataSource(context, Uri.parse(CacheMediaUtils.getFilePathOrUrl(media)))
//                prepareAsync()

                if(isAutoPlay) {
                    setOnPreparedListener {
                        play(true)
                    }
                }
            }
        }
    }

    fun release() = runCatching {
        mediaPlayer?.let {
            stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun stop() = runCatching {
        mediaPlayer?.let {
            if(it.isPlaying) {
                it.stop()
            }
        }
    }

    fun play(isSeekToFirst: Boolean) = runCatching {
        mediaPlayer?.let {
            if(!it.isPlaying) {
                if(isSeekToFirst) {
                    it.seekTo(0)
                }
                it.start()
            }
        }
    }

    fun pause() = runCatching {
        mediaPlayer?.let {
            if(it.isPlaying) {
                it.pause()
            }
        }
    }
}