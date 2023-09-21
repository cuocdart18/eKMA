package com.app.ekma.activities

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import androidx.activity.viewModels
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.AGORA_APP_ID
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.Data
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.databinding.ActivityCallingBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

@AndroidEntryPoint
class CallingActivity : BaseActivity() {
    override val TAG = CallingActivity::class.java.simpleName
    private lateinit var binding: ActivityCallingBinding
    private val viewModel by viewModels<CallingViewModel>()

    private lateinit var agoraEngine: RtcEngine
    private lateinit var localSurfaceView: SurfaceView
    private lateinit var remoteSurfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        setupUI()
        setupVideoSdkEngine {
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
        binding.btnSwitchCamera.setOnClickListener(onClickBtnSwitchCamera)
    }

    private val onClickBtnLeave: (View) -> Unit = {
        finish()
    }

    private val onClickBtnMuteMic: (View) -> Unit = {
        agoraEngine.muteLocalAudioStream(viewModel.isMute)
        viewModel.isMute = !viewModel.isMute
    }

    private val onClickBtnSwitchCamera: (View) -> Unit = {
        agoraEngine.switchCamera()
    }

    private val rtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                showToast("$uid joined")
                setupRemoteVideo(uid)
            }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                showToast("$uid offline")
                remoteSurfaceView.visibility = View.GONE
                finish()
            }
        }

        override fun onTokenPrivilegeWillExpire(token: String?) {
            super.onTokenPrivilegeWillExpire(token)
        }
    }

    private fun setupVideoSdkEngine(
        callback: () -> Unit
    ) {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = AGORA_APP_ID
            config.mEventHandler = rtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine.enableVideo()
            callback()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun joinCall() {
        val option = ChannelMediaOptions()
        option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        setupLocalVideo()
        localSurfaceView.visibility = View.VISIBLE
        agoraEngine.startPreview()
        agoraEngine.joinChannelWithUserAccount(
            viewModel.token,
            viewModel.roomId,
            Data.profile.studentCode,
            option
        )
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        agoraEngine.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
    }

    private fun setupLocalVideo() {
        localSurfaceView = SurfaceView(baseContext)
        binding.localVideoViewContainer.addView(localSurfaceView)
        agoraEngine.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                0
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine.stopPreview()
        agoraEngine.leaveChannel()

        Thread {
            RtcEngine.destroy()
        }.start()
    }
}