package com.app.ekma.ui.score.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.dialogs.BaseDialogFragment
import com.app.ekma.common.KEY_PASS_MINISTUDENT_ID
import com.app.ekma.common.super_utils.app.hideKeyboard
import com.app.ekma.data.models.MiniStudent
import com.app.ekma.databinding.DialogSearchBinding
import com.app.ekma.ui.score.details.StudentDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SearchDataDialogFragment : BaseDialogFragment<DialogSearchBinding>() {
    override val TAG = SearchDataDialogFragment::class.java.simpleName
    private val viewModel by viewModels<SearchDataViewModel>()
    private val searchDataAdapter: SearchDataAdapter by lazy {
        SearchDataAdapter { miniStudent -> onClickListItem(miniStudent) }
    }

    override fun getDataBinding() = DialogSearchBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchAsyncEditText()
        setRecyclerViewProperties()
        binding.searchDataVM = viewModel
        viewModel.showRecentSearchHistory { data ->
            if (data != null) {
                showMiniStudentToUI(data)
            } else {
                showToast("Something went wrong")
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchAsyncEditText() {
        // get data
        viewModel.searchResult.observe(this) { query ->
            viewModel.onSearchEditTextObserved(query) { data ->
                if (data != null) {
                    showMiniStudentToUI(data)
                } else {
                    showToast("Something went wrong")
                }
            }
        }
        binding.edtSearchData.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.queryFlow.emit(it.toString())
            }
        }
        binding.layoutRoot.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                binding.layoutRoot.hideKeyboard()
            }
            false
        }
    }

    private fun setRecyclerViewProperties() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchQuery.layoutManager = linearLayoutManager
        binding.rvSearchQuery.isFocusable = false
        binding.rvSearchQuery.isNestedScrollingEnabled = false
    }

    private fun showMiniStudentToUI(miniStudents: List<MiniStudent>) {
        searchDataAdapter.setMiniStudents(miniStudents)
        binding.rvSearchQuery.adapter = searchDataAdapter
    }

    private fun onClickListItem(miniStudent: MiniStudent) {
        viewModel.insertMiniStudentToDb(miniStudent)
        // action
        val bundle = bundleOf(
            KEY_PASS_MINISTUDENT_ID to miniStudent.id
        )
        parentFragmentManager.commit {
            replace<StudentDetailFragment>(R.id.fragment_container_view, args = bundle)
            setReorderingAllowed(true)
            addToBackStack(StudentDetailFragment::class.java.simpleName)
        }
        dismiss()
    }
}