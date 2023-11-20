package com.app.ekma.ui.chat.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.activities.calling.OutgoingInvitationActivity
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.base.listeners.PaginationScrollListener
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.KEY_PASS_IMAGE_URL
import com.app.ekma.common.TEXT_MSG
import com.app.ekma.common.checkCallPermission
import com.app.ekma.databinding.FragmentChatBinding
import com.app.ekma.firebase.MSG_AUDIO_CALL_TYPE
import com.app.ekma.firebase.MSG_TYPE
import com.app.ekma.firebase.MSG_VIDEO_CALL_TYPE
import com.app.ekma.ui.chat.image_viewer.ImageViewerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    override val TAG = ChatFragment::class.java.simpleName
    private lateinit var binding: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel>()
    private val chatAdapter by lazy { ChatAdapter(requireContext(), imageCallback) }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regisOnBackPressed()
        getBundleData()
        initViews()
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

    private fun initViews() {
        binding.tvNameTitle.text = viewModel.roomId

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvMessages.layoutManager = linearLayoutManager
        chatAdapter.setMessages(viewModel.messages)
        binding.rcvMessages.adapter = chatAdapter

        binding.btnImagePicker.setOnClickListener(onClickBtnImagePicker)
        binding.btnSend.setOnClickListener(onClickBtnSend)
        binding.btnInfo.setOnClickListener(onClickBtnInfo)
        binding.btnAudioCall.setOnClickListener(onClickBtnAudioCall)
        binding.btnVideoCall.setOnClickListener(onClickBtnVideoCall)
        binding.rcvMessages.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMore() {
                viewModel.isLoading = true
                (viewModel.currentPage)++
                loadNextPage()
            }

            override fun isLoading() = viewModel.isLoading

            override fun isLastPage() = viewModel.isLastPage
        })

        viewModel.activeStatus.observe(viewLifecycleOwner) { state ->
            if (state) {
                logError("online")
            } else {
                logError("offline")
            }
        }
    }

    private val onClickBtnSend: (View) -> Unit = {
        viewModel.sendMessage(binding.edtMessageInput.text.toString(), TEXT_MSG)
        binding.edtMessageInput.text.clear()
    }

    private val onClickBtnImagePicker: (View) -> Unit = {
        viewModel.sendImageFromPicker(requireContext())
    }

    private val onClickBtnInfo: (View) -> Unit = {

    }

    private val onClickBtnAudioCall: (View) -> Unit = {
        if (checkCallPermission(requireContext(), MSG_AUDIO_CALL_TYPE)) {
            navigateToOutgoingActivity(MSG_AUDIO_CALL_TYPE)
        } else {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private val onClickBtnVideoCall: (View) -> Unit = {
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
            replace<ImageViewerFragment>(R.id.fragment_container_view, args = bundle)
            setReorderingAllowed(true)
            addToBackStack(ImageViewerFragment::class.java.simpleName)
        }
    }

    private fun initMessaging() {
        viewModel.regisActiveStatusChange()

        viewModel.getTotalMessageCount {
            viewModel.getOlderMessage { itemCount ->
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

        viewModel.observeMessageChanges(addEleCallback)

        viewModel.modifiedMsgPosition.observe(viewLifecycleOwner) { pos ->
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

    private val addEleCallback: (itemCount: Int) -> Unit = { itemCount ->
        chatAdapter.notifyItemRangeInserted(viewModel.messages.size - itemCount, itemCount)
        if (itemCount == 0) {
            binding.rcvMessages.scrollToPosition(viewModel.messages.size - 1)
        } else {
            binding.rcvMessages.smoothScrollToPosition(viewModel.messages.size - 1)
        }
    }

    private fun regisOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}