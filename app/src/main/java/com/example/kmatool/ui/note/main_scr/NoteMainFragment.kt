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
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.data.models.Note
import com.example.kmatool.databinding.FragmentNoteMainBinding
import com.example.kmatool.utils.makeGone
import com.example.kmatool.utils.makeVisible
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
        setUpViews()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpViews() {
        viewModel.selectDay.observe(viewLifecycleOwner) {
            binding.btnSelectDate.text = it
        }
        viewModel.selectTime.observe(viewLifecycleOwner) {
            binding.btnSelectTime.text = it
        }

        binding.btnSave.setOnClickListener { saveNoteToLocalDatabase() }
        binding.btnSelectDate.setOnClickListener {
            openDatePickerDialog { view, year, month, dayOfMonth ->
                viewModel.updateSelectDay(year, month, dayOfMonth)
            }
        }
        binding.btnSelectTime.setOnClickListener {
            openTimePickerDialog() { view, hourOfDay, minute ->
                viewModel.updateSelectTime(hourOfDay, minute)
            }
        }
    }

    private fun saveNoteToLocalDatabase() {
        val title = binding.edtTitle.text.toString().trim()
        val content = binding.edtContent.text.toString().trim()
        val date = binding.btnSelectDate.text.toString().trim()
        val time = binding.btnSelectTime.text.toString().trim()

        if (title.isNotEmpty()) {
            binding.tvEmptyTitleNotify.makeGone()
            val note = Note(title, content, date, time)
            viewModel.saveNoteToLocalDatabase(note) {
                // refresh notesDayMap in Data object
                viewModel.refreshNotesDayMapInDataObject {
                    // on success
                    Toast.makeText(requireContext(), "Tạo ghi chú thành công", Toast.LENGTH_SHORT)
                        .show()
                    // clear data in fragment
                    binding.edtTitle.text.clear()
                    binding.edtContent.text.clear()
                    binding.edtTitle.requestFocus()
                }
            }
        } else {
            if (!binding.tvEmptyTitleNotify.isVisible || !binding.tvEmptyTitleNotify.isGone) {
                binding.tvEmptyTitleNotify.makeVisible()
            }
        }
    }
}