package com.example.kmatool.ui.chat.main

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.base.listeners.PaginationScrollListener
import com.example.kmatool.common.KEY_PASS_CHAT_ROOM_ID
import com.example.kmatool.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    override val TAG = ChatFragment::class.java.simpleName
    private lateinit var binding: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel>()
    private val chatAdapter by lazy { ChatAdapter() }

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
        getBundleData()
        initViews()
        initMessaging()
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

        binding.btnSend.setOnClickListener {
            viewModel.sendMessage(binding.edtMessageInput.text.toString().trim())
            binding.edtMessageInput.text.clear()
        }

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
    }

    private fun initMessaging() {
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

        viewModel.observeMessageDocChanges(addEleCallback)
    }

    private fun loadNextPage() {
        logError("page=${viewModel.currentPage}")
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
}