package com.app.ekma.ui.calling

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.AGORA_APP_ID
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.CALLING_OPERATION
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.pattern.singleton.CallingOperationResponse
import com.app.ekma.common.EARPIECE_AUDIO_ROUTE
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.LEAVE_ROOM
import com.app.ekma.common.MUTE_MIC
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.SPEAKER_AUDIO_ROUTE
import com.app.ekma.common.UNMUTE_MIC
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeVisible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.ActivityAudioCallingBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.Constants.REMOTE_AUDIO_REASON_REMOTE_MUTED
import io.agora.rtc2.Constants.REMOTE_AUDIO_REASON_REMOTE_UNMUTED
import io.agora.rtc2.Constants.REMOTE_AUDIO_STATE_DECODING
import io.agora.rtc2.Constants.REMOTE_AUDIO_STATE_STOPPED
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

@AndroidEntryPoint
class AudioCallingActivity : BaseActivity() {
    override val TAG = AudioCallingActivity::class.java.simpleName
    private lateinit var binding: ActivityAudioCallingBinding
    private val viewModel by viewModels<AudioCallingViewModel>()

    private lateinit var agoraEngine: RtcEngine

    private val isPipSupported by lazy {
        packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        setupUI()
        setupVoiceSdkEngine {
            joinCall()
        }
        setupBackButtonEventListener()
        callingOperationListener()
    }

    private fun getData() {
        val bundle = intent.extras
        bundle?.let {
            viewModel.token = it.getString(CHANNEL_TOKEN, "")
            viewModel.roomId = it.getString(KEY_PASS_CHAT_ROOM_ID, "")
        }
    }

    private fun setupUI() {
        binding.btnLeave.setOnSingleClickListener {
            onClickBtnLeaveRoom()
        }
        binding.btnMuteMic.setOnSingleClickListener {
            onClickBtnMuteMic()
        }
        binding.btnAudioRoute.setOnSingleClickListener {
            onClickBtnAudioRoute()
        }
    }

    private val rtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                showToast("$uid joined")
            }
        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            logError("onLocalAudioStateChanged: state=$state - error=$error")
        }

        override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            logError("onRemoteAudioStateChanged: uid=$uid - state=$state - reason=$reason - elapsed=$elapsed")
            runOnUiThread {
                if (state == REMOTE_AUDIO_STATE_STOPPED && reason == REMOTE_AUDIO_REASON_REMOTE_MUTED) {
                    logError("mute mic roi")
                }
                if (state == REMOTE_AUDIO_STATE_DECODING && reason == REMOTE_AUDIO_REASON_REMOTE_UNMUTED) {
                    logError("unmute mic roi")
                }
            }
        }

        override fun onAudioRouteChanged(routing: Int) {
            logError("routing=$routing")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                showToast("$uid offline")
                finish()
            }
        }
    }

    private fun setupVoiceSdkEngine(callback: () -> Unit) {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = AGORA_APP_ID
            config.mEventHandler = rtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine.setDefaultAudioRoutetoSpeakerphone(false)
            callback()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun joinCall() {
        val option = ChannelMediaOptions()
        option.autoSubscribeAudio = true
        option.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        agoraEngine.joinChannelWithUserAccount(
            viewModel.token,
            viewModel.roomId,
            ProfileSingleton().studentCode,
            option
        )
    }

    private fun onClickBtnLeaveRoom() {
        finish()
    }

    private fun onClickBtnMuteMic() {
        agoraEngine.muteLocalAudioStream(viewModel.isMuteMic)
        if (viewModel.isMuteMic) {
            binding.btnMuteMic.text = "unmute mic"
        } else {
            binding.btnMuteMic.text = "mute mic"
        }
        viewModel.isMuteMic = !viewModel.isMuteMic
    }

    private fun onClickBtnAudioRoute() {
        agoraEngine.setEnableSpeakerphone(viewModel.isSpeakerphone)
        if (viewModel.isSpeakerphone) {
            binding.btnAudioRoute.text = "speakerphone"
        } else {
            binding.btnAudioRoute.text = "earpiece"
        }
        viewModel.isSpeakerphone = !viewModel.isSpeakerphone
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!isPipSupported) {
            return
        }
        updatePictureInPictureParams()?.let { params ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(params)
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
        if (isInPictureInPictureMode) {
            binding.btnMuteMic.makeGone()
            binding.btnAudioRoute.makeGone()
            binding.btnLeave.makeGone()
        } else {
            binding.btnMuteMic.makeVisible()
            binding.btnAudioRoute.makeVisible()
            binding.btnLeave.makeVisible()
        }
    }

    private fun updatePictureInPictureParams(): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(9, 16))
                .setActions(viewModel.getPiPRemoteActions(applicationContext))
            builder.build()
        } else {
            return null
        }
    }

    private fun setupBackButtonEventListener() {
        onBackPressedDispatcher.addCallback(this) {
            updatePictureInPictureParams()?.let { params ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enterPictureInPictureMode(params)
                }
            }
        }
    }

    private fun callingOperationListener() {
        CallingOperationResponse().observe(this) { operation ->
            logInfo("$CALLING_OPERATION:$operation")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return@observe
            }
            if ("" == operation) {
                return@observe
            }
            if (operation == MUTE_MIC || operation == UNMUTE_MIC) {
                onClickBtnMuteMic()
            }
            if (operation == EARPIECE_AUDIO_ROUTE || operation == SPEAKER_AUDIO_ROUTE) {
                onClickBtnAudioRoute()
            }
            if (operation == LEAVE_ROOM) {
                onClickBtnLeaveRoom()
            }
            updatePictureInPictureParams()?.let { params ->
                setPictureInPictureParams(params)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isInPictureInPictureMode) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine.leaveChannel()
        CallingOperationResponse().removeObservers(this)
        CallingOperationResponse.release()

        Thread {
            RtcEngine.destroy()
        }.start()
        BusyCalling.setData(false)
    }
}