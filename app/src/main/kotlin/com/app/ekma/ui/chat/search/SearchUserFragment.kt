package com.app.ekma.ui.chat.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_CHAT_ROOM_ID
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.databinding.FragmentSearchUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchUserFragment : BaseFragment() {
    override val TAG = SearchUserFragment::class.java.simpleName
    private lateinit var binding: FragmentSearchUserBinding
    private val viewModel by viewModels<SearchUserViewModel>()
    private val searchUserAdapter: SearchUserAdapter by lazy { SearchUserAdapter(onItemClicked) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchAsyncEditText()
        setRecyclerViewProperties()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun setSearchAsyncEditText() {
        viewModel.searchResult.observe(viewLifecycleOwner) { query ->
            viewModel.onSearchEditTextObserved(query) { data ->
                if (data != null) {
                    showMiniStudentToUI(data)
                } else {
                    showToast("Something went wrong")
                }
            }
        }
        binding.edtSearchUser.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }
    }

    private fun setRecyclerViewProperties() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvUsersResult.layoutManager = linearLayoutManager
    }

    private fun showMiniStudentToUI(miniStudents: List<MiniStudent>) {
        searchUserAdapter.setMiniStudents(miniStudents)
        binding.rcvUsersResult.adapter = searchUserAdapter
    }

    private val onItemClicked: (studentId: String) -> Unit = { studentId ->
        viewModel.referenceToChatRoom(studentId) { roomId ->
            val bundle = bundleOf(
                KEY_PASS_CHAT_ROOM_ID to roomId
            )
            navigateToFragment(R.id.chatFragment, bundle)
        }
    }
}