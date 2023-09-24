package com.app.ekma.activities

import com.app.ekma.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioCallingViewModel @Inject constructor(

) : BaseViewModel() {
    override val TAG = AudioCallingViewModel::class.java.simpleName

    var token = ""
    var roomId = ""
    var isMuteMic = true
    var isMuteVolume = true

    override fun onCleared() {
        super.onCleared()
    }
}