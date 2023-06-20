package com.example.kmatool.ui.note.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.KEY_PASS_NOTE_MODE
import com.example.kmatool.common.KEY_PASS_NOTE_OBJ
import com.example.kmatool.common.UPDATE_NOTE_MODE
import com.example.kmatool.data.models.Note
import com.example.kmatool.databinding.FragmentNoteDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment() {
    override val TAG = NoteDetailFragment::class.java.simpleName
    private lateinit var binding: FragmentNoteDetailBinding
    private val viewModel by viewModels<NoteDetailViewModel>()
    private var note: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regisOnBackPressed()
        setupView()
        receiveNote()
    }

    private fun receiveNote() {
        val bundle = arguments
        bundle?.let {
            note = it.get(KEY_PASS_NOTE_OBJ) as Note
        }
        binding.note = note
        logInfo("receive oldNote=$note")
    }

    private fun setupView() {
        binding.btnDeleteNote.setOnClickListener { onClickBtnDelete() }
        binding.fabUpdateNote.setOnClickListener { onClickBtnUpdate() }
    }

    private fun onClickBtnUpdate() {
        note?.let {
            // send oldNote to update fragment
            val bundle = bundleOf(
                KEY_PASS_NOTE_OBJ to note,
                KEY_PASS_NOTE_MODE to UPDATE_NOTE_MODE
            )
            // navigate
            navigateToFragment(R.id.noteMainFragment, bundle)
        }
    }

    private fun onClickBtnDelete() {
        note?.let {
            viewModel.onClickDeleteNote(it) {
                viewModel.refreshDataInRecyclerView()
                viewModel.cancelAlarm(it)
                Toast.makeText(requireContext(), "Delete note successfully", Toast.LENGTH_SHORT)
                    .show()
                // reopen ScheduleMainFragment
                navigateToFragment(R.id.action_noteDetailFragment_to_scheduleMainFragment)
            }
        }
    }

    private fun regisOnBackPressed() {
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    navigateToFragment(R.id.action_noteDetailFragment_to_scheduleMainFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}