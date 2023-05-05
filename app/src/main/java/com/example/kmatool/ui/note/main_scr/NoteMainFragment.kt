package com.example.kmatool.ui.note.main_scr

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.ADD_NOTE_MODE
import com.example.kmatool.common.KEY_PASS_NOTE_MODE
import com.example.kmatool.common.KEY_PASS_NOTE_OBJ
import com.example.kmatool.common.UPDATE_NOTE_MODE
import com.example.kmatool.data.models.Note
import com.example.kmatool.databinding.FragmentNoteMainBinding
import com.example.kmatool.common.makeGone
import com.example.kmatool.common.makeVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteMainFragment : BaseFragment() {
    override val TAG = NoteMainFragment::class.java.simpleName
    private lateinit var binding: FragmentNoteMainBinding
    private val viewModel by viewModels<NoteMainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiveNote()
        setUpViews()
    }

    private fun receiveNote() {
        val bundle = arguments
        if (bundle != null) {
            viewModel.noteMode = bundle.getInt(KEY_PASS_NOTE_MODE)
            logInfo("receive note mode=${viewModel.noteMode}")
            if (viewModel.noteMode == UPDATE_NOTE_MODE) {
                viewModel.oldNote = bundle.get(KEY_PASS_NOTE_OBJ) as Note
                logInfo("receive oldNote=${viewModel.oldNote}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpViews() {
        // regis observes
        viewModel.selectDay.observe(viewLifecycleOwner) {
            binding.btnSelectDate.text = it
        }
        viewModel.selectTime.observe(viewLifecycleOwner) {
            binding.btnSelectTime.text = it
        }

        // set text
        if (viewModel.noteMode == ADD_NOTE_MODE) {
            binding.tvNoteHead.text = getString(R.string.tv_note_head_add)
            viewModel.getCurrentDayAndTime()
        } else if (viewModel.noteMode == UPDATE_NOTE_MODE) {
            binding.tvNoteHead.text = getString(R.string.tv_note_head_update)
            binding.edtTitle.setText(viewModel.oldNote?.title)
            binding.edtContent.setText(viewModel.oldNote?.content)
            binding.btnSelectDate.text = viewModel.oldNote?.date
            binding.btnSelectTime.text = viewModel.oldNote?.time
        }

        // regis click listeners
        binding.btnSave.setOnClickListener { saveNoteToLocalDatabase() }
        binding.btnSelectDate.setOnClickListener {
            openDatePickerDialog { _, year, month, dayOfMonth ->
                viewModel.updateSelectDay(year, month, dayOfMonth)
            }
        }
        binding.btnSelectTime.setOnClickListener {
            openTimePickerDialog() { _, hourOfDay, minute ->
                viewModel.updateSelectTime(hourOfDay, minute)
            }
        }
    }

    /*
        action flow
        get oldNote -> save -> refresh Data.notes -> set alarm -> notify to user
        if update mode, cancel alarm before saved
    */
    private fun saveNoteToLocalDatabase() {
        val title = binding.edtTitle.text.toString().trim()
        val content = binding.edtContent.text.toString().trim()
        val date = binding.btnSelectDate.text.toString().trim()
        val time = binding.btnSelectTime.text.toString().trim()

        if (title.isNotEmpty()) {
            binding.tvEmptyTitleNotify.makeGone()
            val note = Note(title, content, date, time)
            viewModel.saveNoteToLocalDatabase(note) {
                if (viewModel.noteMode == UPDATE_NOTE_MODE) {
                    viewModel.oldNote?.let { viewModel.cancelAlarmForOldNote(it) }
                }
                viewModel.refreshNotesDayMapInDataObject {
                    viewModel.setAlarmForNote(note)
                    onUpdateOrAddSuccessfully()
                }
            }
        } else {
            if (!binding.tvEmptyTitleNotify.isVisible || !binding.tvEmptyTitleNotify.isGone) {
                binding.tvEmptyTitleNotify.makeVisible()
            }
        }
    }

    private fun onUpdateOrAddSuccessfully() {
        if (viewModel.noteMode == ADD_NOTE_MODE) {
            Toast.makeText(requireContext(), "Add note successfully", Toast.LENGTH_SHORT)
                .show()
            // clear data in fragment
            binding.edtTitle.text.clear()
            binding.edtContent.text.clear()
            binding.edtTitle.requestFocus()
        } else if (viewModel.noteMode == UPDATE_NOTE_MODE) {
            Toast.makeText(requireContext(), "Update note successfully", Toast.LENGTH_SHORT)
                .show()
            // reopen ScheduleMainFragment
            navigateToFragment(R.id.action_noteMainFragment_to_scheduleMainFragment)
        }
    }
}