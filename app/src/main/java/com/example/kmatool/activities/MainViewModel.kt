package com.example.kmatool.activities

import com.example.kmatool.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName
}