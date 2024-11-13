package com.app.ekma.ui.chat.activity

import com.app.ekma.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : BaseViewModel() {
    override val TAG = ChatViewModel::class.java.simpleName

    init {

    }
}