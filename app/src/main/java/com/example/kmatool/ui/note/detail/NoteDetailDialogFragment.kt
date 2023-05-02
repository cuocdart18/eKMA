package com.example.kmatool.ui.note.detail

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.kmatool.R
import com.example.kmatool.base.dialogs.BaseDialogFragment
import com.example.kmatool.common.KEY_PASS_NOTE_MODE
import com.example.kmatool.common.KEY_PASS_NOTE_OBJ
import com.example.kmatool.common.UPDATE_NOTE_MODE
import com.example.kmatool.data.models.Note
import com.example.kmatool.databinding.DialogNoteDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailDialogFragment : BaseDialogFragment() {
    override val TAG = NoteDetailDialogFragment::class.java.simpleName
    private lateinit var binding: DialogNoteDetailBinding
    private val viewModel by viewModels<NoteDetailViewModel>()
    private var note: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.tvNoteTitle.movementMethod = ScrollingMovementMethod()
        binding.tvNoteContent.movementMethod = ScrollingMovementMethod()
        binding.btnUpdate.setOnClickListener { onClickBtnUpdate() }
        binding.btnDelete.setOnClickListener { onClickBtnDelete() }
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
            dismiss()
        }
    }

    private fun onClickBtnDelete() {
        note?.let {
            viewModel.onClickDeleteNote(it) {
                viewModel.refreshDataInRecyclerView()
                viewModel.cancelAlarm(requireContext(), it)
                Toast.makeText(requireContext(), "Delete oldNote successfully", Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            }
        }
    }
}