package com.example.kmatool.ui.score.search

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.R
import com.example.kmatool.base.dialogs.BaseDialogFragment
import com.example.kmatool.databinding.DialogSearchBinding
import com.example.kmatool.data.models.MiniStudent
import com.example.kmatool.utils.KEY_PASS_MINISTUDENT_ID
import kotlinx.coroutines.*

class SearchDataDialogFragment :
    BaseDialogFragment() {
    override val TAG = SearchDataDialogFragment::class.java.simpleName
    private lateinit var binding: DialogSearchBinding
    private val searchDataViewModel: SearchDataViewModel by lazy {
        ViewModelProvider(requireActivity())[SearchDataViewModel::class.java]
    }
    private val searchDataAdapter: SearchDataAdapter by lazy {
        SearchDataAdapter { miniStudent -> onClickListItem(miniStudent) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchBinding.inflate(inflater, container, false)
        // set search async for EditText
        setSearchAsyncEditText()
        // setup rcv
        setRecyclerViewProperties()
        // setup viewModel, callback
        binding.searchDataVM = searchDataViewModel
        // show recent search history from Room
        searchDataViewModel.showRecentSearchHistory(requireContext()) { ministudents ->
            showMiniStudentToUI(ministudents)
        }
        return binding.root
    }

    private fun setSearchAsyncEditText() {
        logDebug("setSearchAsyncEditText")
        // get data
        searchDataViewModel.searchResult.observe(this) { query ->
            logDebug("observe query = $query")
            searchDataViewModel.onSearchEditTextObserved(query) { data ->
                showMiniStudentToUI(data)
            }
        }
        binding.edtSearchData.doAfterTextChanged {
            lifecycleScope.launch {
                searchDataViewModel.queryChannel.send(it.toString())
            }
        }
    }

    private fun setRecyclerViewProperties() {
        logDebug("setting rcv properties")
        // set recycler view
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
        // insert student into recent db
        searchDataViewModel.insertMiniStudentToDb(requireContext(), miniStudent)
        // navigate
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