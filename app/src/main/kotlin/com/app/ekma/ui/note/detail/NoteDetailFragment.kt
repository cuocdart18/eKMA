package com.app.ekma.ui.note.detail

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_NOTE_MODE
import com.app.ekma.common.KEY_PASS_NOTE_OBJ
import com.app.ekma.common.KEY_PASS_VOICE_AUDIO_NAME
import com.app.ekma.common.UPDATE_NOTE_MODE
import com.app.ekma.common.makeVisible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.data.models.Note
import com.app.ekma.databinding.FragmentNoteDetailBinding
import com.app.ekma.ui.note.audio_player.AudioPlayerFragment
import com.app.ekma.ui.note.main_scr.NoteMainFragment
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment<FragmentNoteDetailBinding>() {
    override val TAG = NoteDetailFragment::class.java.simpleName
    private val viewModel by viewModels<NoteDetailViewModel>()

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun getDataBinding() = FragmentNoteDetailBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiveNote()
        setupView()
    }

    private fun receiveNote() {
        val bundle = arguments
        bundle?.let {
            viewModel.note = it.get(KEY_PASS_NOTE_OBJ) as Note
        }
        binding.note = viewModel.note
    }

    private fun setupView() {
        binding.viewFakeStatus.updateLayoutParams<LinearLayout.LayoutParams> {
            height = getStatusBarHeight
        }
        if (viewModel.note.audioName.isNotEmpty()) {
            if (childFragmentManager.fragments.size == 0) {
                val bundle = bundleOf(
                    KEY_PASS_VOICE_AUDIO_NAME to viewModel.note.audioName
                )
                // add audio player fragment
                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<AudioPlayerFragment>(binding.frmContainerPlayer.id, args = bundle)
                }
            }
            binding.frmContainerPlayer.makeVisible()
        }
        binding.btnDeleteNote.setOnSingleClickListener { onClickBtnDelete() }
        binding.fabUpdateNote.setOnSingleClickListener { onClickBtnUpdate() }
    }

    private fun onClickBtnUpdate() {
        // send oldNote to update fragment
        val bundle = bundleOf(
            KEY_PASS_NOTE_OBJ to viewModel.note,
            KEY_PASS_NOTE_MODE to UPDATE_NOTE_MODE
        )
        // navigate
        parentFragmentManager.commit {
            add<NoteMainFragment>(R.id.fragment_container_view, args = bundle)
            setReorderingAllowed(true)
            addToBackStack(NoteMainFragment::class.java.simpleName)
        }
    }

    private fun onClickBtnDelete() {
        var dialog: Dialog? = null
        fun onClickYes() {
            deleteNote(viewModel.note)
            dialog?.dismiss()
        }

        fun onClickNo() {
            dialog?.dismiss()
        }

        dialog = showAlertDialog(
            R.drawable.inbox_cleanup_red_500dp,
            "Xoá ghi chú ?",
            "",
            "Đồng ý",
            "Huỷ bỏ",
            { onClickYes() },
            { onClickNo() },
            false
        )
        dialog.show()
    }

    private fun deleteNote(note: Note) {
        viewModel.onClickDeleteNote(requireContext(), note) {
            viewModel.refreshDataInRecyclerView()
            viewModel.cancelAlarm(note)
            // reopen ScheduleMainFragment
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onPause() {
        super.onPause()
        callback.remove()
    }
}