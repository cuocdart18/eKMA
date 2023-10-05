package com.app.ekma.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.AGORA_APP_ID
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.Data
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.databinding.ActivityAudioCallingBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

@AndroidEntryPoint
class AudioCallingActivity : BaseActivity() {
    override val TAG = AudioCallingActivity::class.java.simpleName
    private lateinit var binding: ActivityAudioCallingBinding
    private val viewModel by viewModels<AudioCallingViewModel>()

    private lateinit var agoraEngine: RtcEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        setupUI()
        setupVoiceSdkEngine {
            joinCall()
        }
    }

    private fun getData() {
        val bundle = intent.extras
        bundle?.let {
            viewModel.token = it.getString(CHANNEL_TOKEN, "")
            viewModel.roomId = it.getString(KEY_PASS_CHAT_ROOM_ID, "")
        }
    }

    private fun setupUI() {
        binding.btnLeave.setOnClickListener(onClickBtnLeave)
        binding.btnMuteMic.setOnClickListener(onClickBtnMuteMic)
        binding.btnAudioRoute.setOnClickListener(onClickBtnAudioRoute)
    }

    private val onClickBtnLeave: (View) -> Unit = {
        finish()
    }

    private val onClickBtnMuteMic: (View) -> Unit = {
        agoraEngine.muteLocalAudioStream(viewModel.isMuteMic)
        viewModel.isMuteMic = !viewModel.isMuteMic
    }

    private val onClickBtnAudioRoute: (View) -> Unit = {
    }

    private val rtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                showToast("$uid joined")
            }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                showToast("$uid offline")
                finish()
            }
        }

        override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed)
            logError("$uid - $state - $reason - $elapsed")
        }

        override fun onTokenPrivilegeWillExpire(token: String?) {
            super.onTokenPrivilegeWillExpire(token)
        }
    }

    private fun setupVoiceSdkEngine(
        callback: () -> Unit
    ) {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = AGORA_APP_ID
            config.mEventHandler = rtcEventHandler
            agoraEngine = RtcEngine.create(config)
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
            Data.profile.studentCode,
            option
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine.leaveChannel()

        Thread {
            RtcEngine.destroy()
        }.start()
    }
}