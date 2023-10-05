package com.app.ekma.ui.note.main_scr

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.ADD_NOTE_MODE
import com.app.ekma.common.KEY_PASS_NOTE_MODE
import com.app.ekma.common.KEY_PASS_NOTE_OBJ
import com.app.ekma.common.KEY_PASS_VOICE_AUDIO_NAME
import com.app.ekma.common.PAUSE_RECORDING
import com.app.ekma.common.RESUME_RECORDING
import com.app.ekma.common.START_RECORDING
import com.app.ekma.common.UPDATE_NOTE_MODE
import com.app.ekma.common.formatRecordingTimer
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeInVisible
import com.app.ekma.common.makeVisible
import com.app.ekma.data.models.Note
import com.app.ekma.databinding.FragmentNoteMainBinding
import com.app.ekma.ui.note.audio_player.AudioPlayerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteMainFragment : BaseFragment() {
    override val TAG = NoteMainFragment::class.java.simpleName
    private lateinit var binding: FragmentNoteMainBinding
    private val viewModel by viewModels<NoteMainViewModel>()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { audioPmsCallback(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteMainBinding.inflate(inflater, container, false)
        return binding.root
    }

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

    private fun setUpViews() {
        regisDayTimeTextObserver()
        setTextTitleHeader()
        setInputTextHelper()
        regisOnClickListeners()
    }

    private fun regisDayTimeTextObserver() {
        viewModel.selectDay.observe(viewLifecycleOwner) {
            binding.tvSelectDate.text = it
        }
        viewModel.selectTime.observe(viewLifecycleOwner) {
            binding.tvSelectTime.text = it
        }
    }

    private fun setTextTitleHeader() {
        if (viewModel.noteMode == ADD_NOTE_MODE) {
            binding.tvNoteHead.text = getString(R.string.tv_note_head_add)
            viewModel.getCurrentDayAndTime()
        } else if (viewModel.noteMode == UPDATE_NOTE_MODE) {
            binding.tvNoteHead.text = getString(R.string.tv_note_head_update)
            binding.edtTitle.setText(viewModel.oldNote.title)
            binding.edtContent.setText(viewModel.oldNote.content)
            binding.tvSelectDate.text = viewModel.oldNote.date
            binding.tvSelectTime.text = viewModel.oldNote.time
        }
    }

    private fun setInputTextHelper() {
        binding.edtTitle.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty())
                binding.tilTitle.error = getString(R.string.empty_title_notify)
            else
                binding.tilTitle.error = ""
        }
    }

    private fun regisOnClickListeners() {
        setupForBaseLayout()
        addVoicePlayerLayout()
        setupForRecorderLayout()
    }

    private fun setupForBaseLayout() {
        binding.btnSave.setOnClickListener { onClickBtnSave() }
        binding.tvSelectDate.setOnClickListener {
            openDatePickerDialog { _, year, month, dayOfMonth ->
                viewModel.updateSelectDay(year, month, dayOfMonth)
            }
        }
        binding.tvSelectTime.setOnClickListener {
            openTimePickerDialog { _, hourOfDay, minute ->
                viewModel.updateSelectTime(hourOfDay, minute)
            }
        }
    }

    private fun addVoicePlayerLayout() {
        if (viewModel.noteMode == UPDATE_NOTE_MODE) {
            if (viewModel.oldNote.audioName.isNotEmpty()) {
                if (childFragmentManager.fragments.size == 0) {
                    val bundle = bundleOf(
                        KEY_PASS_VOICE_AUDIO_NAME to viewModel.oldNote.audioName
                    )
                    // add audio player fragment
                    childFragmentManager.commit {
                        setReorderingAllowed(true)
                        add<AudioPlayerFragment>(binding.frmContainerPlayer.id, args = bundle)
                    }
                }
                binding.btnDeleteAudioOldNote.setOnClickListener {
                    viewModel.deleteAudioOldNote(requireContext())
                    binding.layoutAudioPlayer.makeGone()
                }
                binding.layoutAudioPlayer.makeVisible()
            }
        }
    }

    private fun setupForRecorderLayout() {
        viewModel.initRecorder(uiCallback, updateDuration)
        binding.layoutVoiceRecorder.tvDuration.text =
            formatRecordingTimer(viewModel.getCurrentDuration())
        viewModel.getStateOfRecorder(uiCallback)
        binding.swVoiceRecorder.setOnCheckedChangeListener { _, isChecked ->
            checkAudioPermission(isChecked) {
                if (isChecked) {
                    binding.layoutVoiceRecorder.rootView.makeVisible()
                } else {
                    binding.layoutVoiceRecorder.rootView.makeGone()
                    // if recorder has been started, stop it
                    deleteRecorder()
                }
            }
        }
        binding.layoutVoiceRecorder.btnRecord.setOnClickListener {
            viewModel.onClickBtnRecord(requireContext())
        }
        binding.layoutVoiceRecorder.btnDelete.setOnClickListener {
            deleteRecorder()
        }
    }

    private val updateDuration: (duration: Int) -> Unit = {
        binding.layoutVoiceRecorder.tvDuration.text = formatRecordingTimer(it)
    }

    private val uiCallback: (state: Int) -> Unit = {
        when (it) {
            RESUME_RECORDING -> {
                binding.layoutVoiceRecorder.btnDelete.makeVisible()
                binding.layoutVoiceRecorder.btnFitRecordUi.makeInVisible()
                binding.layoutVoiceRecorder.btnRecord.setImageResource(R.drawable.pause_record_circle_fill_red)
            }

            PAUSE_RECORDING -> {
                binding.layoutVoiceRecorder.btnDelete.makeVisible()
                binding.layoutVoiceRecorder.btnFitRecordUi.makeInVisible()
                binding.layoutVoiceRecorder.btnRecord.setImageResource(R.drawable.play_record_circle_fill_red)
            }

            START_RECORDING -> {
                binding.layoutVoiceRecorder.btnDelete.makeVisible()
                binding.layoutVoiceRecorder.btnFitRecordUi.makeInVisible()
                binding.layoutVoiceRecorder.btnRecord.setImageResource(R.drawable.pause_record_circle_fill_red)
            }
        }
    }

    private fun checkAudioPermission(
        isChecked: Boolean,
        grantedCallback: () -> Unit
    ) {
        when (PackageManager.PERMISSION_GRANTED) {
            checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) -> {
                grantedCallback()
            }

            else -> {
                if (isChecked) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.RECORD_AUDIO
                    )
                }
            }
        }
    }

    private fun audioPmsCallback(isGranted: Boolean) {
        if (isGranted) {
            binding.swVoiceRecorder.isChecked = true
            binding.layoutVoiceRecorder.rootView.makeVisible()
        }
    }

    private fun deleteRecorder() {
        viewModel.deleteRecord()
        resetLayoutVoiceRecorder()
    }

    private fun resetLayoutVoiceRecorder() {
        binding.layoutVoiceRecorder.btnDelete.makeGone()
        binding.layoutVoiceRecorder.btnFitRecordUi.makeGone()
        binding.layoutVoiceRecorder.btnRecord.setImageResource(R.drawable.record_circle_fill_red)
        binding.layoutVoiceRecorder.tvDuration.text = "00:00.00"
    }

    private fun onClickBtnSave() {
        val title = binding.edtTitle.text.toString().trim()
        val content = binding.edtContent.text.toString().trim()
        val date = binding.tvSelectDate.text.toString().trim()
        val time = binding.tvSelectTime.text.toString().trim()
        var audioName = ""
        if (viewModel.noteMode == UPDATE_NOTE_MODE) {
            audioName = viewModel.oldNote.audioName
            if (viewModel.oldNote.audioName.isNotEmpty()
                and
                viewModel.isRecording()
            ) {
                showToast("Chọn 1 trong 2 bản ghi âm")
                return
            }
        }

        if (title.isNotEmpty()) {
            viewModel.saveRecord(requireContext()) { fileName ->
                if (fileName.isNotEmpty()) {
                    audioName = fileName
                }
                resetLayoutVoiceRecorder()
                val note = Note(title, content, audioName, date, time)
                saveNote(note)
            }

            var dialog: Dialog? = null
            fun onClickYes() {
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

    private fun saveNote(note: Note) {
        viewModel.saveNote(note) {
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

    override fun onStop() {
        super.onStop()
        viewModel.pauseRecorder()
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
}