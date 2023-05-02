package com.example.kmatool.ui.note.update

import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteUpdateViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : BaseViewModel() {
    override val TAG = NoteUpdateViewModel::class.java.simpleName


}