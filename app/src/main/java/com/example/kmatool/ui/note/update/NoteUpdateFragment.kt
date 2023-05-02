package com.example.kmatool.ui.note.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.KEY_PASS_NOTE_OBJ
import com.example.kmatool.data.models.Note
import com.example.kmatool.databinding.FragmentNoteMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteUpdateFragment : BaseFragment() {
    override val TAG = NoteUpdateFragment::class.java.simpleName
    private lateinit var binding: FragmentNoteMainBinding
    private val viewModel by viewModels<NoteUpdateViewModel>()
    private var note: Note? = null

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
        bundle?.let {
            note = it.get(KEY_PASS_NOTE_OBJ) as Note
        }
        logInfo("receive note=$note")
    }

    private fun setUpViews() {
        if (note != null) {
            binding.edtTitle.setText(note!!.title)
            binding.edtContent.setText(note!!.content)
            binding.btnSelectDate.text = note!!.date
            binding.btnSelectTime.text = note!!.time
        }


    }

}