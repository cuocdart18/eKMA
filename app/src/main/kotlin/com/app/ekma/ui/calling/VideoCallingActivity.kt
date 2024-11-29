package com.app.ekma.ui.calling

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.SurfaceView
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.app.ekma.R
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.AGORA_APP_ID
import com.app.ekma.common.CALLING_OPERATION
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.LEAVE_ROOM
import com.app.ekma.common.MUTE_CAMERA
import com.app.ekma.common.MUTE_MIC
import com.app.ekma.common.UNMUTE_CAMERA
import com.app.ekma.common.UNMUTE_MIC
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeVisible
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.pattern.singleton.CallingOperationResponse
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.ActivityVideoCallingBinding
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_RECEIVER_CODE
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.Constants.REMOTE_AUDIO_REASON_REMOTE_MUTED
import io.agora.rtc2.Constants.REMOTE_AUDIO_REASON_REMOTE_UNMUTED
import io.agora.rtc2.Constants.REMOTE_AUDIO_STATE_DECODING
import io.agora.rtc2.Constants.REMOTE_AUDIO_STATE_STOPPED
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_PLAYING
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_STOPPED
import io.agora.rtc2.Constants.VIDEO_MIRROR_MODE_AUTO
import io.agora.rtc2.Constants.VIDEO_MIRROR_MODE_ENABLED
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

@AndroidEntryPoint
class VideoCallingActivity : BaseActivity() {
    override val TAG = VideoCallingActivity::class.java.simpleName
    private lateinit var binding: ActivityVideoCallingBinding
    private val viewModel by viewModels<VideoCallingViewModel>()

    private lateinit var agoraEngine: RtcEngine
    private lateinit var localSurfaceView: SurfaceView
    private lateinit var remoteSurfaceView: SurfaceView

    private val isPipSupported by lazy {
        packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityVideoCallingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getData()
        setupUI()
        setupVideoSdkEngine {
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
            viewModel.friendCode =
                it.getString(MSG_INVITER_CODE) ?: it.getString(MSG_RECEIVER_CODE, "")
        }
    }

    private fun setupUI() {
        viewModel.getSenderInformation()
        binding.layoutControl.visible(true)

        binding.btnBack.setOnSingleClickListener {
            updatePictureInPictureParams()?.let { params ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enterPictureInPictureMode(params)
                }
            }
        }
        binding.btnLeave.setOnSingleClickListener {
            binding.layoutControl.gone(true) {
                onClickLeaveRoom()
            }
        }
        binding.btnMuteMic.setOnSingleClickListener {
            viewModel.toggleMicState()
        }
        binding.btnSwitchCamera.setOnSingleClickListener {
            viewModel.toggleSwitchCamera()
        }
        binding.btnMuteCamera.setOnSingleClickListener {
            viewModel.toggleCameraState()
        }

        collectLatestFlow(viewModel.imageAvatarUri) {
            it?.let {
                Glide.with(this)
                    .load(it)
                    .override(SMALL_AVT_W_H, SMALL_AVT_W_H)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(binding.imvAvatar)
            }
        }
        collectLatestFlow(viewModel.friendName) {
            binding.tvSenderName.text = it
        }
        collectLatestFlow(viewModel.isMuteMic) {
            agoraEngine.muteLocalAudioStream(it)
            if (it) {
                binding.btnMuteMic.setImageResource(R.drawable.ic_micro_mute)
            } else {
                binding.btnMuteMic.setImageResource(R.drawable.ic_micro_unmute)
            }
        }
        collectLatestFlow(viewModel.isMuteCamera) {
            agoraEngine.muteLocalVideoStream(it)
            if (it) {
                localSurfaceView.makeGone()
                binding.btnMuteCamera.setImageResource(R.drawable.ic_camera_mute)
            } else {
                localSurfaceView.makeVisible()
                binding.btnMuteCamera.setImageResource(R.drawable.ic_camera_unmute)
            }
        }
        collectLatestFlow(viewModel.isCameraFront) {
            agoraEngine.switchCamera()
        }
        collectLatestFlow(viewModel.callTimer) {
            runCatching {
                binding.tvCallDescript.text = it
            }
        }
    }

    private val rtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
                viewModel.startCallTimer()
            }
        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            logError("onLocalAudioStateChanged: state=$state - error=$error")
        }

        override fun onLocalVideoStateChanged(
            source: Constants.VideoSourceType?,
            state: Int,
            error: Int
        ) {
            logError("onLocalVideoStateChanged: state=$state - error=$error")
        }

        override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            logError("onRemoteAudioStateChanged: uid=$uid - state=$state - reason=$reason - elapsed=$elapsed")
            runOnUiThread {
                if (state == REMOTE_AUDIO_STATE_STOPPED && reason == REMOTE_AUDIO_REASON_REMOTE_MUTED) {
                    binding.btnMuteMicFriend.setImageResource(R.drawable.ic_micro_mute)
                }
                if (state == REMOTE_AUDIO_STATE_DECODING && reason == REMOTE_AUDIO_REASON_REMOTE_UNMUTED) {
                    binding.btnMuteMicFriend.setImageResource(R.drawable.ic_micro_unmute)
                }
            }
        }

        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            logError("onRemoteVideoStateChanged: uid=$uid - state=$state - reason=$reason - elapsed=$elapsed")
            runOnUiThread {
                if (state == REMOTE_VIDEO_STATE_STOPPED && reason == REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED) {
                    remoteSurfaceView.makeGone()
                    binding.btnMuteCameraFriend.setImageResource(R.drawable.ic_camera_mute)
                }
                if (state == REMOTE_VIDEO_STATE_PLAYING && reason == REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED) {
                    remoteSurfaceView.makeVisible()
                    binding.btnMuteCameraFriend.setImageResource(R.drawable.ic_camera_unmute)
                }
            }
        }

        override fun onAudioRouteChanged(routing: Int) {
            logError("routing=$routing")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                remoteSurfaceView.makeGone()
                finishAndRemoveTask()
            }
        }
    }

    private fun setupVideoSdkEngine(callback: () -> Unit) {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = AGORA_APP_ID
            config.mEventHandler = rtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine.enableVideo()
            agoraEngine.setDefaultAudioRoutetoSpeakerphone(true)
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
        localSurfaceView.makeVisible()
        agoraEngine.startPreview()
        agoraEngine.joinChannelWithUserAccount(
            viewModel.token,
            viewModel.roomId,
            ProfileSingleton().studentCode,
            option
        )
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView.setZOrderMediaOverlay(false)
        binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        agoraEngine.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                uid
            )
        )
        agoraEngine.setRemoteRenderMode(
            uid,
            VideoCanvas.RENDER_MODE_HIDDEN,
            VIDEO_MIRROR_MODE_AUTO
        )
    }

    private fun setupLocalVideo() {
        localSurfaceView = SurfaceView(baseContext)
        localSurfaceView.setZOrderMediaOverlay(true)
        localSurfaceView.setZOrderOnTop(true)
        binding.localVideoViewContainer.addView(localSurfaceView)
        agoraEngine.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
        agoraEngine.setLocalRenderMode(
            VideoCanvas.RENDER_MODE_HIDDEN,
            VIDEO_MIRROR_MODE_AUTO
        )
    }

    private fun onClickLeaveRoom() {
        finishAndRemoveTask()
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
            binding.layoutHeader.gone(true)
            binding.layoutControl.gone(true)
            binding.btnBack.makeGone()
            binding.localVideoViewContainer.makeGone()
        } else {
            binding.layoutHeader.visible(true)
            binding.layoutControl.visible(true)
            binding.btnBack.makeVisible()
            binding.localVideoViewContainer.makeVisible()
        }
    }

    private fun updatePictureInPictureParams(): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val rect = Rect()
            binding.remoteVideoViewContainer.getGlobalVisibleRect(rect)
            val builder = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(9, 16))
                .setSourceRectHint(rect)
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
                viewModel.toggleMicState()
            }
            if (operation == MUTE_CAMERA || operation == UNMUTE_CAMERA) {
                viewModel.toggleCameraState()
            }
            if (operation == LEAVE_ROOM) {
                onClickLeaveRoom()
            }
            updatePictureInPictureParams()?.let { params ->
                setPictureInPictureParams(params)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isInPictureInPictureMode) {
            finishAndRemoveTask()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopCallTimer()
        CallingOperationResponse().removeObservers(this)
        CallingOperationResponse.release()
        agoraEngine.stopPreview()
        agoraEngine.leaveChannel()

        Thread {
            RtcEngine.destroy()
        }.start()
        BusyCalling.setData(false)
    }
}