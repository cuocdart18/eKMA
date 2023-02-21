package com.example.kmatool.fragments.score

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.R
import com.example.kmatool.adapter.score.SearchDataAdapter
import com.example.kmatool.databinding.DialogSearchBinding
import com.example.kmatool.models.score.MiniStudent
import com.example.kmatool.utils.KEY_PASS_MINISTUDENT_ID
import com.example.kmatool.utils.SCALE_LAYOUT_SEARCH_DATA_DIALOG_X
import com.example.kmatool.utils.SCALE_LAYOUT_SEARCH_DATA_DIALOG_Y
import com.example.kmatool.utils.textChanges
import com.example.kmatool.view_model.score.SearchDataViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOn

class SearchDataDialogFragment :
    DialogFragment() {
    private val TAG = SearchDataDialogFragment::class.java.simpleName
    private lateinit var binding: DialogSearchBinding
    private val navController: NavController by lazy { findNavController() }
    private val searchDataViewModel: SearchDataViewModel by lazy {
        ViewModelProvider(requireActivity())[SearchDataViewModel::class.java]
    }
    private val searchDataAdapter: SearchDataAdapter by lazy {
        SearchDataAdapter { miniStudent ->
            onClickListItem(
                miniStudent
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create view $TAG")
        binding = DialogSearchBinding.inflate(inflater, container, false)
        // set search async for EditText
        setSearchAsyncEditText()

        // set theme for dialog
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
        // setup UI
        setScaleUI()
        setRecyclerViewProperties()

        // setup viewModel, callback
        binding.searchDataVM = searchDataViewModel

        // show recent search history from Room
        context?.applicationContext?.let {
            searchDataViewModel.showRecentSearchHistory(it) { ministudents ->
                showMiniStudentToUI(ministudents)
            }
        }
    }

    @OptIn(FlowPreview::class, DelicateCoroutinesApi::class)
    private fun setSearchAsyncEditText() {
        Log.d(TAG, "setting search async edit text")
        GlobalScope.launch {
            binding.edtSearchData.textChanges()
                .debounce(500)
                .filterNot { it.isNullOrBlank() }
                .distinctUntilChanged()
                .flowOn(Dispatchers.IO)
                .collect { text ->
                    searchDataViewModel.onSearchEditTextEmitted(text.toString()) { ministudents ->
                        showMiniStudentToUI(ministudents)
                    }
                }
        }
    }

    private fun setScaleUI() {
        Log.d(TAG, "setting scale UI")
        val window = dialog?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.getSize(size)
        window?.setLayout(
            ((size.x * SCALE_LAYOUT_SEARCH_DATA_DIALOG_X).toInt()),
            ((size.y * SCALE_LAYOUT_SEARCH_DATA_DIALOG_Y).toInt())
        )
        window?.setGravity(Gravity.CENTER)
    }

    private fun setRecyclerViewProperties() {
        Log.d(TAG, "setting rcv properties")
        // set recycler view
        val linearLayoutManager = LinearLayoutManager(context)
        binding.rvSearchQuery.layoutManager = linearLayoutManager
        binding.rvSearchQuery.isFocusable = false
        binding.rvSearchQuery.isNestedScrollingEnabled = false
    }

    private fun showMiniStudentToUI(miniStudents: List<MiniStudent>) {
        Log.d(TAG, "show data UI student")
        searchDataAdapter.setMiniStudents(miniStudents)
        binding.rvSearchQuery.adapter = searchDataAdapter
    }

    private fun onClickListItem(miniStudent: MiniStudent) {
        Log.i(TAG, "on click student = $miniStudent")
        // insert student into recent db
        context?.applicationContext?.let {
            searchDataViewModel.insertMiniStudentToDb(
                it,
                miniStudent
            )
        }
        // navigate
        navigateStudentDetailFragment(miniStudent.id)
    }

    private fun navigateStudentDetailFragment(id: String) {
        Log.d(TAG, "navigate detail fragment with student = $id")
        // action
        val bundle = bundleOf(
            KEY_PASS_MINISTUDENT_ID to id
        )
        navController.navigate(R.id.studentDetailFragment, bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}