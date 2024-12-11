package com.app.ekma.ui.calling

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.app.ekma.R
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.AGORA_APP_ID
import com.app.ekma.common.CALLING_OPERATION
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.EARPIECE_AUDIO_ROUTE
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.KEY_PASS_IS_IN_PIP
import com.app.ekma.common.LEAVE_ROOM
import com.app.ekma.common.MUTE_MIC
import com.app.ekma.common.SPEAKER_AUDIO_ROUTE
import com.app.ekma.common.UNMUTE_MIC
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.pattern.singleton.CallingOperationResponse
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.ActivityAudioCallingBinding
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_RECEIVER_CODE
import com.bumptech.glide.Glide
import com.cuocdat.activityutils.getStatusBarHeight
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
    private var isFirstOpen = true
    private val isPipSupported by lazy {
        packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityAudioCallingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            viewModel.friendCode =
                it.getString(MSG_INVITER_CODE) ?: it.getString(MSG_RECEIVER_CODE, "")
        }
    }

    private fun setupUI() {
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }
        viewModel.getSenderInformation()

        binding.btnLeave.setOnSingleClickListener {
            binding.layoutControl.gone(true) {
                onClickBtnLeaveRoom()
            }
        }
        binding.btnMuteMic.setOnSingleClickListener {
            viewModel.toggleMicState()
        }
        binding.btnAudioRoute.setOnSingleClickListener {
            viewModel.toggleSpeakerState()
        }
        binding.layoutControl.visible(true) {}

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
        collectLatestFlow(viewModel.isSpeakerphone) {
            agoraEngine.setEnableSpeakerphone(it)
            if (it) {
                binding.btnAudioRoute.setImageResource(R.drawable.ic_speakerphone)
            } else {
                binding.btnAudioRoute.setImageResource(R.drawable.ic_earpiece)
            }
        }
        collectLatestFlow(viewModel.callTimer) {
            runCatching {
                binding.tvCallDescript.text = it
            }
        }
    }

    private val rtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            viewModel.startCallTimer()
        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            logError("onLocalAudioStateChanged: state=$state - error=$error")
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

        override fun onAudioRouteChanged(routing: Int) {
            logError("routing=$routing")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                finishAndRemoveTask()
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
        finishAndRemoveTask()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstOpen) {
            isFirstOpen = false
            intent.extras?.let {
                val isInPiP = it.getBoolean(KEY_PASS_IS_IN_PIP, false)
                if (isInPiP) {
                    binding.layoutControl.gone(true)
                    updatePictureInPictureParams()?.let { params ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            enterPictureInPictureMode(params)
                        }
                    }
                } else {
                    binding.layoutControl.visible(true) {}
                }
            }
        }
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
            binding.layoutControl.gone(true)
        } else {
            binding.layoutControl.visible(true) {}
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
                viewModel.toggleMicState()
            }
            if (operation == EARPIECE_AUDIO_ROUTE || operation == SPEAKER_AUDIO_ROUTE) {
                viewModel.toggleSpeakerState()
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
            finishAndRemoveTask()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopCallTimer()
        agoraEngine.leaveChannel()
        CallingOperationResponse().removeObservers(this)
        CallingOperationResponse.release()

        Thread {
            RtcEngine.destroy()
        }.start()
        BusyCalling.setData(false)
    }
}