package com.app.ekma.ui.chat.search

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.invisible
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.app.showKeyboard
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.databinding.FragmentSearchUserBinding
import com.app.ekma.ui.chat.main.ChatFragment
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchUserFragment : BaseFragment<FragmentSearchUserBinding>() {
    override val TAG = SearchUserFragment::class.java.simpleName
    private val viewModel by viewModels<SearchUserViewModel>()
    private val searchUserAdapter by lazy { SearchUserAdapter(requireContext(), onItemClicked) }

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun getDataBinding() = FragmentSearchUserBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edtSearchUser.requestFocus()
        binding.edtSearchUser.showKeyboard()
        regisOnBackPressed()
        setSearchAsyncEditText()
        setRecyclerViewProperties()
    }

    private fun setSearchAsyncEditText() {
        binding.edtSearchUser.doAfterTextChanged {
            lifecycleScope.launch {
                val text = it.toString()
                viewModel.setShowSearchIcon(text.isNotEmpty())
                viewModel.queryFlow.emit(text)
            }
        }

        collectLatestFlow(viewModel.searchResult) { query ->
            viewModel.onSearchEditTextObserved(query)
        }

        collectLatestFlow(viewModel.miniStudentsRes) { data ->
            if (data.isEmpty()) {
                binding.tvNoMsg.visible(true)
            } else {
                binding.tvNoMsg.invisible(true)
            }
            searchUserAdapter.setMiniStudents(data)
        }

        collectLatestFlow(viewModel.showSearchIcon) {
            if (it) {
                binding.layoutIcon.visible(true)
            } else {
                binding.layoutIcon.invisible(true)
            }
        }
    }

    private fun setRecyclerViewProperties() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvUsersResult.layoutManager = linearLayoutManager
        binding.rcvUsersResult.adapter = searchUserAdapter
    }

    private val onItemClicked: (String) -> Unit = { studentCode ->
        viewModel.referenceToChatRoom(studentCode) { roomId ->
            val bundle = bundleOf(
                KEY_PASS_CHAT_ROOM_ID to roomId
            )
            parentFragmentManager.commit {
                replace<ChatFragment>(R.id.frmContainer, args = bundle)
                setReorderingAllowed(true)
                addToBackStack(ChatFragment::class.java.simpleName)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }
    }

    private fun regisOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        binding.btnBack.performClick {
            parentFragmentManager.popBackStack()
        }
    }
}