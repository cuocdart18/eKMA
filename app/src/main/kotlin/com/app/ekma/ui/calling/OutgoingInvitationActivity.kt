package com.app.ekma.ui.calling

import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ekma.R
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.CALLING_OPERATION
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.HANG_UP
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.KEY_PASS_IS_IN_PIP
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.pattern.singleton.CallingOperationResponse
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.ActivityOugoingInvitationBinding
import com.app.ekma.firebase.MSG_ACCEPT
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_RECEIVER_CODE
import com.app.ekma.firebase.MSG_REJECT
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
import com.bumptech.glide.Glide
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

const val SMALL_AVT_W_H = 100

@AndroidEntryPoint
class OutgoingInvitationActivity : BaseActivity() {
    override val TAG = OutgoingInvitationActivity::class.java.simpleName
    private lateinit var binding: ActivityOugoingInvitationBinding
    private val viewModel by viewModels<OutgoingInvitationViewModel>()

    private var isKeepCalling = false
    private val isPipSupported by lazy {
        packageManager.hasSystemFeature(
            PackageManager.FEATURE_PICTURE_IN_PICTURE
        )
    }

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityOugoingInvitationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getData()
        setupUI()
        addCollect()
        viewModel.getReceivers()
        callingOperationListener()
        // set isBusyCalling state in this device
        BusyCalling.setData(true)
    }

    private fun getData() {
        val bundlePasser = intent.extras
        bundlePasser?.let {
            viewModel.roomId = it.getString(KEY_PASS_CHAT_ROOM_ID, "")
            viewModel.callType = it.getString(MSG_TYPE, "")
        }
    }

    private fun setupUI() {
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }

        binding.btnCancel.setOnSingleClickListener {
            binding.btnCancel.isEnabled = false
            viewModel.cancelInvitation()
        }

        onBackPressedDispatcher.addCallback(this) {
            updatePictureInPictureParams()?.let { params ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enterPictureInPictureMode(params)
                }
            }
        }
    }

    private fun addCollect() {
        // cancel activity after not receive a response
        collectLatestFlow(viewModel.isExpiredActivation) {
            if (it) {
                viewModel.cancelInvitation()
            }
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
            binding.tvReceiverName.text = it
        }
        collectLatestFlow(viewModel.onCancelInvite) {
            if (it) {
                delay(1000L)
                finishAndRemoveTask()
            }
        }
    }

    private val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            bundle?.let {
                val operation = it.getString(MSG_OPERATION, "")
                logError(operation)
                when (operation) {
                    MSG_ACCEPT -> {
                        viewModel.createChannelAndGetToken(getTokenCallback)
                    }

                    MSG_REJECT -> {
                        BusyCalling.setData(false)
                        finishAndRemoveTask()
                    }
                }
            }
        }
    }

    private val getTokenCallback: (String, String) -> Unit = { token, friendCode ->
        if (token.isEmpty()) {
            viewModel.cancelInvitation()
        } else {
            viewModel.sendChannelTokenToReceiver(token) {
                // move to calling activity
                val intentAcceptCall = if (viewModel.callType == MSG_VIDEO_CALL_TYPE) {
                    Intent(this@OutgoingInvitationActivity, VideoCallingActivity::class.java)
                } else {
                    Intent(this@OutgoingInvitationActivity, AudioCallingActivity::class.java)
                }
                // put data
                val bundleAcceptCall = bundleOf(
                    KEY_PASS_CHAT_ROOM_ID to viewModel.roomId,
                    CHANNEL_TOKEN to token,
                    MSG_RECEIVER_CODE to friendCode,
                    KEY_PASS_IS_IN_PIP to isInPictureInPictureMode
                )
                intentAcceptCall.putExtras(bundleAcceptCall)
                isKeepCalling = true

                if (isInPictureInPictureMode) {
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                    launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(launchIntent)
                }

                finish()
                startActivity(intentAcceptCall)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(MSG_OPERATION)
        )
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
            binding.btnCancel.gone(true)
        } else {
            binding.btnCancel.visible(true) {}
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

    private fun callingOperationListener() {
        CallingOperationResponse().observe(this) { operation ->
            logInfo("$CALLING_OPERATION:$operation")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return@observe
            }
            if ("" == operation) {
                return@observe
            }
            if (operation == HANG_UP) {
                binding.btnCancel.isEnabled = false
                viewModel.cancelInvitation()
            }
            updatePictureInPictureParams()?.let { params ->
                setPictureInPictureParams(params)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(invitationResponseReceiver)
        if (isInPictureInPictureMode and !isKeepCalling) {
            binding.btnCancel.isEnabled = false
            viewModel.cancelInvitationByWorker(this) {
                delay(1000L)
                finishAndRemoveTask()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CallingOperationResponse().removeObservers(this)
        CallingOperationResponse.release()
    }
}