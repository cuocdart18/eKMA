package com.example.kmatool.view_model.schedule

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.database.PeriodRepository
import com.example.kmatool.local.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScheduleMainViewModel : ViewModel() {
    private val TAG = ScheduleMainViewModel::class.java.simpleName

    fun testGetDataFromLocal(context: Context) {
        // test read data
        val dataStoreManager = DataStoreManager(context)
        viewModelScope.launch(Dispatchers.IO) {
            // get login state
            launch {
                dataStoreManager.isLoginDataStoreFlow.collect {
                    Log.i(TAG, "login state = $it")
                }
            }
            // get profile
            launch {
                dataStoreManager.profileDataStoreFlow.collect {
                    Log.i(TAG, "profile = $it")
                }
            }
            // get schedule
            launch {
                val periodRepository = PeriodRepository(context)
                val data = periodRepository.getPeriods()
                Log.i(TAG, "periods from database = $data")
            }
        }
    }
}