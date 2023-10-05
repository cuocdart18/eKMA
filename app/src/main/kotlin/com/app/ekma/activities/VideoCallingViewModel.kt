package com.app.ekma.activities

import com.app.ekma.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoCallingViewModel @Inject constructor(

) : BaseViewModel() {
    override val TAG = VideoCallingViewModel::class.java.simpleName

    var token = ""
    var roomId = ""
    var isMuteMic = true
    var isMuteCamera = true

    override fun onCleared() {
        super.onCleared()
    }
}