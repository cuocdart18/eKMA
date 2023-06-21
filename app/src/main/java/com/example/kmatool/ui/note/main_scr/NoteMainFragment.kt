package com.example.kmatool.ui.note.main_scr

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.ADD_NOTE_MODE
import com.example.kmatool.common.KEY_PASS_NOTE_MODE
import com.example.kmatool.common.KEY_PASS_NOTE_OBJ
import com.example.kmatool.common.UPDATE_NOTE_MODE
import com.example.kmatool.data.models.Note
import com.example.kmatool.databinding.FragmentNoteMainBinding
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
            if (viewModel.noteMode == UPDATE_NOTE_MODE) {
                regisOnBackPressed()
                viewModel.oldNote = bundle.get(KEY_PASS_NOTE_OBJ) as Note
            }
        }
    }

    private fun regisOnBackPressed() {
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    navigateToFragment(R.id.action_noteMainFragment_to_scheduleMainFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpViews() {
        // regis observes
        viewModel.selectDay.observe(viewLifecycleOwner) {
            binding.tvSelectDate.text = it
        }
        viewModel.selectTime.observe(viewLifecycleOwner) {
            binding.tvSelectTime.text = it
        }

        // set text
        if (viewModel.noteMode == ADD_NOTE_MODE) {
            binding.tvNoteHead.text = getString(R.string.tv_note_head_add)
            viewModel.getCurrentDayAndTime()
        } else if (viewModel.noteMode == UPDATE_NOTE_MODE) {
            binding.tvNoteHead.text = getString(R.string.tv_note_head_update)
            binding.edtTitle.setText(viewModel.oldNote?.title)
            binding.edtContent.setText(viewModel.oldNote?.content)
            binding.tvSelectDate.text = viewModel.oldNote?.date
            binding.tvSelectTime.text = viewModel.oldNote?.time
        }

        // set input title
        binding.edtTitle.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty())
                binding.tilTitle.error = getString(R.string.empty_title_notify)
            else
                binding.tilTitle.error = ""
        }

        // regis click listeners
        binding.btnSave.setOnClickListener { onClickBtnSave() }
        binding.tvSelectDate.setOnClickListener {
            openDatePickerDialog { _, year, month, dayOfMonth ->
                viewModel.updateSelectDay(year, month, dayOfMonth)
            }
        }
        binding.tvSelectTime.setOnClickListener {
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
    private fun onClickBtnSave() {
        val title = binding.edtTitle.text.toString().trim()
        val content = binding.edtContent.text.toString().trim()
        val date = binding.tvSelectDate.text.toString().trim()
        val time = binding.tvSelectTime.text.toString().trim()

        if (title.isNotEmpty()) {
            val note = Note(title, content, date, time)
            var dialog: Dialog? = null
            fun onClickYes() {
                saveNoteToLocalDatabase(note)
                dialog?.dismiss()
            }

            fun onClickNo() {
                dialog?.dismiss()
            }

            dialog = showAlertDialog(
                R.drawable.confirmed_bro_red_500dp,
                "Thành công!",
                "Ghi chú đã được lưu, để nhận thông báo, bạn cần xem phần cài đặt",
                "Đóng",
                "",
                { onClickYes() },
                { onClickNo() },
                true
            )
            dialog.show()
        }
    }

    private fun saveNoteToLocalDatabase(note: Note) {
        viewModel.saveNoteToLocalDatabase(note) {
            if (viewModel.noteMode == UPDATE_NOTE_MODE) {
                viewModel.oldNote?.let { viewModel.cancelAlarmForOldNote(it) }
            }
            viewModel.refreshNotesDayMapInDataObject {
                viewModel.setAlarmForNote(note)
                onUpdateOrAddSuccessfully()
            }
        }
    }

    private fun onUpdateOrAddSuccessfully() {
        if (viewModel.noteMode == ADD_NOTE_MODE) {
            // clear data in fragment
            binding.edtTitle.text?.clear()
            binding.edtContent.text?.clear()
            binding.edtTitle.requestFocus()
            binding.tilTitle.error = ""
        } else if (viewModel.noteMode == UPDATE_NOTE_MODE) {
            // reopen ScheduleMainFragment
            navigateToFragment(R.id.action_noteMainFragment_to_scheduleMainFragment)
        }
    }
}