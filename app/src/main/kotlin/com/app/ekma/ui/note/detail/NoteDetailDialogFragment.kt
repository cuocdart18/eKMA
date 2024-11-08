package com.app.ekma.ui.note.detail

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.dialogs.BaseDialogFragment
import com.app.ekma.common.KEY_PASS_NOTE_MODE
import com.app.ekma.common.KEY_PASS_NOTE_OBJ
import com.app.ekma.common.UPDATE_NOTE_MODE
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.data.models.Note
import com.app.ekma.databinding.DialogNoteDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailDialogFragment : BaseDialogFragment<DialogNoteDetailBinding>() {
    override val TAG = NoteDetailDialogFragment::class.java.simpleName
    private val viewModel by viewModels<NoteDetailViewModel>()
    private var note: Note? = null

    override fun getDataBinding() = DialogNoteDetailBinding.inflate(layoutInflater)

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
        binding.btnUpdate.setOnSingleClickListener { onClickBtnUpdate() }
        binding.btnDelete.setOnSingleClickListener { onClickBtnDelete() }
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
            viewModel.onClickDeleteNote(requireContext(), it) {
                viewModel.refreshDataInRecyclerView()
                viewModel.cancelAlarm(it)
                Toast.makeText(requireContext(), "Delete note successfully", Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            }
        }
    }
}