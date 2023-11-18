package com.app.ekma.ui.chat.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.FROM_POSITION
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.TO_POSITION
import com.app.ekma.databinding.FragmentListChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListChatFragment : BaseFragment() {
    override val TAG = ListChatFragment::class.java.simpleName
    private lateinit var binding: FragmentListChatBinding
    private val viewModel by viewModels<ListChatViewModel>()
    private val listChatAdapter by lazy { ListChatAdapter(requireContext(), onClickChatRoomItem) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        showListChatRoom()
    }

    private fun initView() {
        binding.btnSearch.setOnClickListener { navigateToFragment(R.id.searchUserFragment) }
        binding.rcvListChat.layoutManager = LinearLayoutManager(requireContext())
        listChatAdapter.setChatRooms(viewModel.rooms)
        binding.rcvListChat.adapter = listChatAdapter
    }

    private fun showListChatRoom() {
        if (viewModel.rooms.isEmpty()) {
            viewModel.listenChatRoomsChanges()
        }
        viewModel.addedRoomPos.observe(viewLifecycleOwner) { pos ->
            if (pos == -1) return@observe
            listChatAdapter.notifyItemInserted(0)
        }
        viewModel.movedRoomPos.observe(viewLifecycleOwner) { pos ->
            val from = pos[FROM_POSITION] ?: -1
            val to = pos[TO_POSITION] ?: -1
            if (from == -1 || to == -1) return@observe
            listChatAdapter.notifyItemRangeChanged(to, from - to + 1)
        }
        viewModel.modifiedRoomPos.observe(viewLifecycleOwner) { pos ->
            if (pos == -1) return@observe
            listChatAdapter.notifyItemChanged(pos)
        }
    }

    private val onClickChatRoomItem: (roomId: String) -> Unit = {
        viewModel.modifySeenMembersInRoom(it)
        val bundle = bundleOf(
            KEY_PASS_CHAT_ROOM_ID to it
        )
        navigateToFragment(R.id.chatFragment, bundle)
    }
}