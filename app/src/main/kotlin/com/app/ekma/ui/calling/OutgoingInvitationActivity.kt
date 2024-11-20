package com.app.ekma.ui.calling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.ekma.R
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.CHANNEL_TOKEN
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.ActivityOugoingInvitationBinding
import com.app.ekma.firebase.MSG_ACCEPT
import com.app.ekma.firebase.MSG_OPERATION
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
            binding.btnCancel.isEnabled = false
            viewModel.cancelInvitation()
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
                finish()
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
                        finish()
                    }
                }
            }
        }
    }

    private val getTokenCallback: (String) -> Unit = { token ->
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
}