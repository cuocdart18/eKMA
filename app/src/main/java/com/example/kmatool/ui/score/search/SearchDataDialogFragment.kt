package com.example.kmatool.ui.score.search

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.R
import com.example.kmatool.base.dialogs.BaseDialogFragment
import com.example.kmatool.databinding.DialogSearchBinding
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.common.KEY_PASS_MINISTUDENT_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SearchDataDialogFragment :
    BaseDialogFragment() {
    override val TAG = SearchDataDialogFragment::class.java.simpleName
    private lateinit var binding: DialogSearchBinding
    private val viewModel by viewModels<SearchDataViewModel>()
    private val searchDataAdapter: SearchDataAdapter by lazy {
        SearchDataAdapter { miniStudent -> onClickListItem(miniStudent) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchAsyncEditText()
        setRecyclerViewProperties()
        binding.searchDataVM = viewModel
        viewModel.showRecentSearchHistory { miniStudents ->
            showMiniStudentToUI(miniStudents)
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun setSearchAsyncEditText() {
        logDebug("setSearchAsyncEditText")
        // get data
        viewModel.searchResult.observe(this) { query ->
            logDebug("observe query = $query")
            viewModel.onSearchEditTextObserved(query) { data ->
                showMiniStudentToUI(data)
            }
        }
        binding.edtSearchData.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }
    }

    private fun setRecyclerViewProperties() {
        logDebug("setting rcv properties")
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchQuery.layoutManager = linearLayoutManager
        binding.rvSearchQuery.isFocusable = false
        binding.rvSearchQuery.isNestedScrollingEnabled = false
    }

    private fun showMiniStudentToUI(miniStudents: List<MiniStudent>) {
        logDebug("show data UI student")
        searchDataAdapter.setMiniStudents(miniStudents)
        binding.rvSearchQuery.adapter = searchDataAdapter
    }

    private fun onClickListItem(miniStudent: MiniStudent) {
        logDebug("on click student = $miniStudent")
        viewModel.insertMiniStudentToDb(miniStudent)
        navigateStudentDetailFragment(miniStudent.id)
    }

    private fun navigateStudentDetailFragment(id: String) {
        logDebug("navigate detail fragment with student = $id")
        // action
        val bundle = bundleOf(
            KEY_PASS_MINISTUDENT_ID to id
        )
        navigateToFragment(R.id.studentDetailFragment, bundle)
    }
}