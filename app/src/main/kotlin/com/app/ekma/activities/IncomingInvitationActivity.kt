package com.app.ekma.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.checkCallPermission
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomingInvitationActivity : BaseActivity() {
    override val TAG = IncomingInvitationActivity::class.java.simpleName
    private lateinit var binding: ActivityIncomingInvitationBinding
    private val viewModel by viewModels<IncomingInvitationViewModel>()
    private val requestAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            logError("request audio permission = $it")
            if (it) onAccept() else onReject()
        }
    private val requestVideoPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            logError("request video permission = $it")
            if (it.getOrDefault(Manifest.permission.RECORD_AUDIO, false)
                and
                it.getOrDefault(Manifest.permission.CAMERA, false)
            ) onAccept() else onReject()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        setupUI()
    }

    private fun getData() {
        val bundlePasser = intent.extras
        bundlePasser?.let {
            viewModel.inviterCode = it.getString(MSG_INVITER_CODE, "")
            viewModel.callType = it.getString(MSG_TYPE, "")
        }
    }

    private fun setupUI() {
        binding.tvSenderName.text = viewModel.inviterCode
        binding.btnAccept.setOnClickListener(onClickBtnAccept)
        binding.btnReject.setOnClickListener(onClickBtnReject)
        viewModel.isExpiredActivation.observe(this) { isExpired ->
            if (isExpired)
                finish()
        }
    }

    private val onClickBtnAccept: (View) -> Unit = {
        if (checkCallPermission(this, viewModel.callType)) {
            onAccept()
        } else {
            if (viewModel.callType == MSG_AUDIO_CALL_TYPE) {
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else if (viewModel.callType == MSG_VIDEO_CALL_TYPE) {
                requestVideoPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA
                    )
                )
            }
            showToast("Bạn cần cấp quyền")
        }
    }

    private val onClickBtnReject: (View) -> Unit = {
        onReject()
    }

    private fun onAccept() {
        viewModel.sendMessageInvitationResponse(MSG_ACCEPT) {
            viewModel.channelTokenPending()
        }
    }

    private fun onReject() {
        viewModel.sendMessageInvitationResponse(MSG_REJECT) {
            finish()
        }
    }

    private val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            bundle?.let {
                val operation = it.getString(MSG_OPERATION, "")
                logError(operation)
                when (operation) {
                    MSG_CANCEL -> {
                        finish()
                    }

                    MSG_SEND_CHANNEL_TOKEN -> {
                        val token = it.getString(CHANNEL_TOKEN, "")
                        val roomId = it.getString(KEY_PASS_CHAT_ROOM_ID, "")
                        val intentAcceptCall =
                            Intent(this@IncomingInvitationActivity, CallingActivity::class.java)
                        // put data
                        val bundleAcceptCall = bundleOf(
                            MSG_TYPE to viewModel.callType,
                            CHANNEL_TOKEN to token,
                            KEY_PASS_CHAT_ROOM_ID to roomId,
                        )
                        intentAcceptCall.putExtras(bundleAcceptCall)
                        startActivity(intentAcceptCall)
                        finish()
                    }
                }
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

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(invitationResponseReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isExpiredActivation.removeObservers(this)
    }
}