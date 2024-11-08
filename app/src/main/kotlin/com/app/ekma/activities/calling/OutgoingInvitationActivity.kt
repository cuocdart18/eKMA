package com.app.ekma.activities.calling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.ActivityOugoingInvitationBinding
import com.app.ekma.firebase.MSG_ACCEPT
import com.app.ekma.firebase.MSG_OPERATION
import com.app.ekma.firebase.MSG_REJECT
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OutgoingInvitationActivity : BaseActivity() {
    override val TAG = OutgoingInvitationActivity::class.java.simpleName
    private lateinit var binding: ActivityOugoingInvitationBinding
    private val viewModel by viewModels<OutgoingInvitationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOugoingInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        setupUI()
        initVideoInvitation()
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
        // cancel activity after not receive a response
        viewModel.isExpiredActivation.observe(this) { isExpired ->
            if (isExpired) {
                viewModel.cancelInvitation {
                    finish()
                }
            }
        }
        binding.btnCancel.setOnSingleClickListener {
            viewModel.cancelInvitation {
                finish()
            }
        }
    }

    private fun initVideoInvitation() {
        viewModel.getReceivers { name ->
            binding.tvReceiverName.text = "Call ${viewModel.callType} for $name"
            viewModel.inviteReceiver()
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
                        finish()
                    }
                }
            }
        }
    }

    private val getTokenCallback: (String) -> Unit = { token ->
        if (token.isEmpty()) {
            viewModel.cancelInvitation {
                finish()
            }
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
                    CHANNEL_TOKEN to token
                )
                intentAcceptCall.putExtras(bundleAcceptCall)
                startActivity(intentAcceptCall)
                finish()
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