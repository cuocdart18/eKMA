package com.example.kmatool.ui.score.main_scr

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmatool.data.repositories.ScoreRepository
import com.example.kmatool.data.models.Statistic
import com.example.kmatool.utils.OK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreMainViewModel : ViewModel() {
    private val TAG = ScoreMainViewModel::class.java.simpleName
    private val scoreRepository = ScoreRepository()
    private var restoreStatistic: Statistic? = null

    val statisticOF = ObservableField<Statistic>()

    fun getStatisticData() {
        Log.d(TAG, "init statistics data")

        restoreStatistic?.let { data ->
            statisticOF.set(data)
            return
        }

        // action
        viewModelScope.launch(Dispatchers.IO) {
            val result = scoreRepository.getStatistics()
            Log.d(TAG, "getStatisticData status code = ${result.statusCode}")

            withContext(Dispatchers.Main) {
                if (result.statusCode == OK) {
                    val data = result.data
                    // restore data
                    if (data != null) {
                        restoreStatistic = data
                    }
                    Log.i(TAG, "data = $data")
                    // update data to UI
                    statisticOF.set(data)
                }
            }
        }
    }
}