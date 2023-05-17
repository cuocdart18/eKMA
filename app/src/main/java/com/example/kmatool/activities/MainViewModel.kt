package com.example.kmatool.activities

import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.app_data.DataLocalManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataLocalManager: DataLocalManager
) : BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

}