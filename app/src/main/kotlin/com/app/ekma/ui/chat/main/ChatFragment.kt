package com.app.ekma.ui.chat.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateOvershootInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.base.listeners.PaginationScrollListener
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.KEY_PASS_IMAGE_URL
import com.app.ekma.common.TEXT_MSG
import com.app.ekma.common.afterTextChanged
import com.app.ekma.common.checkCallPermission
import com.app.ekma.common.makeInVisible
import com.app.ekma.common.makeVisible
import com.app.ekma.common.pattern.singleton.BusyCalling
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.MarginType
import com.app.ekma.common.super_utils.animation.animateMargin
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.invisible
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.common.super_utils.number_string_date.dp
import com.app.ekma.databinding.FragmentChatBinding
import com.app.ekma.firebase.MSG_AUDIO_CALL_TYPE
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
import com.app.ekma.ui.calling.OutgoingInvitationActivity
import com.app.ekma.ui.chat.image_viewer.ImageViewerFragment
import com.bumptech.glide.Glide
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {
    override val TAG = ChatFragment::class.java.simpleName
    private val viewModel by viewModels<ChatViewModel>()
    private val chatAdapter by lazy { ChatAdapter(requireContext(), imageCallback) }

    private val androidRoot by lazy {
        requireActivity().window?.decorView?.findViewById<View>(android.R.id.content)
    }
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        val rootView = androidRoot
        rootView?.getWindowVisibleDisplayFrame(rect)
        val screenHeight = binding.root.height
        val keyboardHeight = screenHeight - rect.bottom // calculate size
        removeListen()
        if (viewModel.showScrollPopUp.value) {
            setShowScrollPopUp(true)
        }
        binding.layoutFooter.animateMargin(
            marginType = MarginType.BOTTOM,
            duration = 250,
            newMargin = if (keyboardHeight < 0) 0 else keyboardHeight,
            onEnd = ::addListen
        )
    }

    private val requestAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                navigateToOutgoingActivity(MSG_AUDIO_CALL_TYPE)
            }
        }
    private val requestVideoPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.getOrDefault(Manifest.permission.RECORD_AUDIO, false)
                and
                it.getOrDefault(Manifest.permission.CAMERA, false)
            ) {
                navigateToOutgoingActivity(MSG_VIDEO_CALL_TYPE)
            }
        }

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun getDataBinding() = FragmentChatBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regisOnBackPressed()
        getBundleData()
        initViews()
        viewModel.getChatRoomNameAndImage()
        viewModel.getMembersCode {
            if (viewModel.messages.isEmpty()) {
                initMessaging()
            }
        }
    }

    private fun getBundleData() {
        val bundle = arguments
        bundle?.let {
            viewModel.roomId = it.getString(KEY_PASS_CHAT_ROOM_ID) ?: ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        if (viewModel.messages.isEmpty()) {
            binding.rcvMessages.makeInVisible()
            binding.tvNoMsg.makeVisible()
        } else {
            binding.rcvMessages.makeVisible()
            binding.tvNoMsg.makeInVisible()
        }

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvMessages.layoutManager = linearLayoutManager
        chatAdapter.setMessages(viewModel.messages)
        binding.rcvMessages.adapter = chatAdapter

        collectLatestFlow(viewModel.roomName) {
            binding.tvNameTitle.text = it
        }
        collectLatestFlow(viewModel.activeStatus) {
            if (it) {
                binding.tvActiveStatus.text = "Online"
                binding.imvActive.setImageResource(R.drawable.ic_dot_online)
            } else {
                binding.tvActiveStatus.text = "Offline"
                binding.imvActive.setImageResource(R.drawable.ic_dot_offline)
            }
        }
        collectLatestFlow(viewModel.roomImage) {
            it?.let {
                Glide.with(requireContext())
                    .load(it)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(binding.imvAvatar)
            }
        }
        collectLatestFlow(viewModel.enableSendMsg) {
            if (it) binding.btnSend.visible(true) {}
            else binding.btnSend.invisible(true)
        }
        collectLatestFlow(viewModel.showScrollPopUp) {
            setShowScrollPopUp(state = it)
        }
        binding.edtMessageInput.afterTextChanged { viewModel.setEnableBtnSendMsg(it.isNotEmpty()) }

        binding.btnScroll.setOnSingleClickListener {
            try {
                binding.rcvMessages.smoothScrollToPosition(viewModel.messages.size - 1)
            } catch (e: Exception) {
                binding.rcvMessages.scrollToPosition(viewModel.messages.size - 1)
            }
        }
        binding.btnBack.setOnSingleClickListener {
            binding.btnBack.gone(true) {
                parentFragmentManager.popBackStack()
            }
        }
        binding.btnImagePicker.setOnSingleClickListener(onClickBtnImagePicker)
        binding.btnSend.setOnSingleClickListener(onClickBtnSend)
        binding.btnInfo.setOnSingleClickListener(onClickBtnInfo)
        binding.btnAudioCall.setOnSingleClickListener(onClickBtnAudioCall)
        binding.btnVideoCall.setOnSingleClickListener(onClickBtnVideoCall)
        binding.rcvMessages.addOnScrollListener(
            object : PaginationScrollListener(linearLayoutManager) {
                override fun loadMore() {
                    viewModel.isLoading = true
                    (viewModel.currentPage)++
                    loadNextPage()
                }

                override fun isLoading() = viewModel.isLoading

                override fun isLastPage() = viewModel.isLastPage

                override fun setShowScrollPopUp(state: Boolean) {
                    viewModel.setShowScrollPopUp(state)
                }
            })
    }

    private val onClickBtnSend: (View) -> Unit = {
        viewModel.sendMessage(
            context = requireContext(),
            content = binding.edtMessageInput.text.toString(),
            type = TEXT_MSG
        )
        binding.edtMessageInput.text?.clear()
    }

    private val onClickBtnImagePicker: (View) -> Unit = {
        viewModel.sendImageFromPicker(requireContext())
    }

    private val onClickBtnInfo: (View) -> Unit = {
        showToast("Coming soon")
    }

    private val onClickBtnAudioCall: (View) -> Unit = {
        if (BusyCalling()) {
            showToast("Bạn đang thực hiện cuộc gọi")
        } else {
            if (checkCallPermission(requireContext(), MSG_AUDIO_CALL_TYPE)) {
                navigateToOutgoingActivity(MSG_AUDIO_CALL_TYPE)
            } else {
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private val onClickBtnVideoCall: (View) -> Unit = {
        if (BusyCalling()) {
            showToast("Bạn đang thực hiện cuộc gọi")
        } else {
            if (checkCallPermission(requireContext(), MSG_VIDEO_CALL_TYPE)) {
                navigateToOutgoingActivity(MSG_VIDEO_CALL_TYPE)
            } else {
                requestVideoPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA
                    )
                )
            }
        }
    }

    private fun navigateToOutgoingActivity(type: String) {
        if (viewModel.roomId.isNotEmpty()) {
            val intent = Intent(requireContext(), OutgoingInvitationActivity::class.java)
            val bundle = bundleOf(
                KEY_PASS_CHAT_ROOM_ID to viewModel.roomId,
                MSG_TYPE to type
            )
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private val imageCallback: (imgUrl: String) -> Unit = { imgUrl ->
        val bundle = bundleOf(
            KEY_PASS_IMAGE_URL to imgUrl
        )
        parentFragmentManager.commit {
            replace<ImageViewerFragment>(R.id.frmContainer, args = bundle)
            setReorderingAllowed(true)
            addToBackStack(ImageViewerFragment::class.java.simpleName)
        }
    }

    private fun initMessaging() {
        viewModel.regisActiveStatusChange()

        viewModel.getTotalMessageCount {
            viewModel.getOlderMessage { itemCount ->
                if (itemCount != 0) {
                    binding.rcvMessages.makeVisible()
                    binding.tvNoMsg.makeInVisible()
                }
                chatAdapter.notifyItemRangeInserted(0, itemCount)
                binding.rcvMessages.scrollToPosition(viewModel.messages.size - 1)
                viewModel.isLoading = false
                if (viewModel.currentPage < viewModel.totalPage) {
                    chatAdapter.addHeaderLoading()
                } else {
                    viewModel.isLastPage = true
                }
            }
        }

        viewModel.observeMessageChanges { itemCount ->
            binding.rcvMessages.makeVisible()
            binding.tvNoMsg.makeInVisible()
            chatAdapter.notifyItemRangeInserted(viewModel.messages.size - itemCount, itemCount)
            if (itemCount == 0) {
                binding.rcvMessages.scrollToPosition(viewModel.messages.size - 1)
            } else {
                binding.rcvMessages.smoothScrollToPosition(viewModel.messages.size - 1)
            }
        }

        collectLatestFlow(viewModel.modifiedMsgPosition) { pos ->
            if (pos != -1) {
                chatAdapter.notifyItemRangeChanged(pos, viewModel.messages.size - pos)
            }
        }
    }

    private fun loadNextPage() {
        Handler().postDelayed({
            chatAdapter.removeHeaderLoading()
            viewModel.getOlderMessage { itemCount ->
                if (viewModel.currentPage < viewModel.totalPage) {
                    // itemCount + 1 (first item for loading)
                    chatAdapter.notifyItemRangeInserted(0, itemCount + 1)
                } else {
                    chatAdapter.notifyItemRangeInserted(0, itemCount)
                }
                viewModel.isLoading = false
                if (viewModel.currentPage < viewModel.totalPage) {
                    chatAdapter.addHeaderLoading()
                } else {
                    viewModel.isLastPage = true
                }
            }
        }, 600L)
    }

    override fun onStart() {
        super.onStart()
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }
    }

    override fun onResume() {
        super.onResume()
        addListen()
    }

    private fun regisOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun addListen() {
        androidRoot?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onPause() {
        super.onPause()
        removeListen()
    }

    private fun removeListen() {
        androidRoot?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    private fun setShowScrollPopUp(state: Boolean) {
        binding.btnScroll.clearAnimation()
        val xPos = (binding.layoutRoot.width / 2 - binding.btnScroll.width / 2).toFloat()
        val yPosShow = binding.layoutFooter.y - 50f.dp
        val yPosHide = (binding.layoutRoot.height + binding.btnScroll.height).toFloat()
        if (state) {
            binding.btnScroll.makeInVisible()
            binding.btnScroll.animate()
                .x(xPos)
                .y(yPosHide)
                .withEndAction {
                    binding.btnScroll.makeVisible()
                    binding.btnScroll.animate()
                        .x(xPos)
                        .y(yPosShow)
                        .setInterpolator(AnticipateOvershootInterpolator())
                        .setDuration(300L)
                }
        } else {
            binding.btnScroll.animate()
                .x(xPos)
                .y(yPosHide)
                .setInterpolator(AnticipateOvershootInterpolator())
                .setDuration(300L)
        }
    }
}