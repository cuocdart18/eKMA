package com.app.ekma.activities

import com.app.ekma.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CallingViewModel @Inject constructor(

) : BaseViewModel() {
    override val TAG = CallingViewModel::class.java.simpleName

    var token = ""
    var roomId = ""
    var isMute = true

    override fun onCleared() {
        super.onCleared()
    }
}