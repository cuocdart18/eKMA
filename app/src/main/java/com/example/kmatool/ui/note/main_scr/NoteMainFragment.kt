package com.example.kmatool.ui.note.main_scr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentNoteMainBinding

class NoteMainFragment : BaseFragment() {
    override val TAG = NoteMainFragment::class.java.simpleName
    private lateinit var binding: FragmentNoteMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteMainBinding.inflate(inflater, container, false)
        return binding.root
    }
}