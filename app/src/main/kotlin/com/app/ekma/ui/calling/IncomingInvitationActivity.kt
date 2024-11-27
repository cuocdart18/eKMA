package com.app.ekma.ui.calling

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ekma.R
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.checkCallPermission
import com.app.ekma.common.makeGone
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.ActivityIncomingInvitationBinding
import com.app.ekma.firebase.MSG_ACCEPT
import com.app.ekma.firebase.MSG_AUDIO_CALL_TYPE
import com.app.ekma.firebase.MSG_CANCEL
import com.app.ekma.firebase.MSG_INVITER_CODE
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_REJECT
import com.app.ekma.firebase.MSG_SEND_CHANNEL_TOKEN
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class IncomingInvitationActivity : BaseActivity() {
    override val TAG = IncomingInvitationActivity::class.java.simpleName
    private lateinit var binding: ActivityIncomingInvitationBinding
    private val viewModel by viewModels<IncomingInvitationViewModel>()
    private val requestAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) onAccept() else onReject()
        }
    private val requestVideoPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.getOrDefault(Manifest.permission.RECORD_AUDIO, false)
                and
                it.getOrDefault(Manifest.permission.CAMERA, false)
            ) onAccept() else onReject()
        }

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityIncomingInvitationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getData()
        setupUI()
        addCollect()
        BusyCalling.setData(true)
    }

    private fun getData() {
        val bundlePasser = intent.extras
        bundlePasser?.let {
            viewModel.inviterCode = it.getString(MSG_INVITER_CODE, "")
            viewModel.callType = it.getString(MSG_TYPE, "")
            viewModel.setCallTypeName(viewModel.callType)
        }
    }

    private fun setupUI() {
        viewModel.getSenderInformation()
        binding.btnAccept.setOnSingleClickListener(onClickBtnAccept)
        binding.btnReject.setOnSingleClickListener(onClickBtnReject)
        onBackPressedDispatcher.addCallback(this) {
            binding.btnReject.isEnabled = false
            onReject()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addCollect() {
        collectLatestFlow(viewModel.isExpiredActivation) {
            if (it) {
                delay(1000L)
                finish()
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
            binding.tvSenderName.text = it
        }
        collectLatestFlow(viewModel.callTypeName) {
            binding.tvCallFrom.text = "$it call from eKMA"
        }
    }

    private val onClickBtnAccept: (View) -> Unit = {
        if (checkCallPermission(this, viewModel.callType)) {
            onAccept()
        } else {
            when (viewModel.callType) {
                MSG_AUDIO_CALL_TYPE -> {
                    requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }

                MSG_VIDEO_CALL_TYPE -> {
                    requestVideoPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA
                        )
                    )
                }
            }
            showToast("Bạn cần cấp quyền")
        }
    }

    private val onClickBtnReject: (View) -> Unit = {
        binding.btnReject.isEnabled = false
        onReject()
    }

    private fun onAccept() {
        viewModel.sendMessageInvitationResponse(MSG_ACCEPT) {
            viewModel.channelTokenPending()
            binding.btnAccept.gone(true) { binding.tvAccept.makeGone() }
            binding.btnReject.gone(true) { binding.tvReject.makeGone() }
        }
    }

    private fun onReject() {
        viewModel.sendMessageInvitationResponse(MSG_REJECT) {
            BusyCalling.setData(false)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(MSG_OPERATION)
        )
    }

    private val invitationResponseReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            bundle?.let {
                val operation = it.getString(MSG_OPERATION, "")
                logError(operation)
                when (operation) {
                    MSG_CANCEL -> {
                        BusyCalling.setData(false)
                        finish()
                    }

                    MSG_SEND_CHANNEL_TOKEN -> {
                        val token = it.getString(CHANNEL_TOKEN, "")
                        val roomId = it.getString(KEY_PASS_CHAT_ROOM_ID, "")

                        val intentAcceptCall = if (viewModel.callType == MSG_VIDEO_CALL_TYPE) {
                            Intent(
                                this@IncomingInvitationActivity,
                                VideoCallingActivity::class.java
                            )
                        } else {
                            Intent(
                                this@IncomingInvitationActivity,
                                AudioCallingActivity::class.java
                            )
                        }
                        // put data
                        val bundleAcceptCall = bundleOf(
                            CHANNEL_TOKEN to token,
                            KEY_PASS_CHAT_ROOM_ID to roomId,
                            MSG_INVITER_CODE to viewModel.inviterCode,
                        )
                        intentAcceptCall.putExtras(bundleAcceptCall)
                        startActivity(intentAcceptCall)
                        finish()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(invitationResponseReceiver)
    }
}