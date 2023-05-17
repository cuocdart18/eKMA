package com.example.kmatool.ui.score.main_scr

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.example.kmatool.base.viewmodel.BaseViewModel
import com.example.kmatool.data.repositories.ScoreRepository
import com.example.kmatool.data.models.Statistic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreMainViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository
) : BaseViewModel() {
    override val TAG = ScoreMainViewModel::class.java.simpleName
    private var restoreStatistic: Statistic? = null

    val statisticOF = ObservableField<Statistic>()

    /*fun getStatisticData() {
        logDebug("init statistics data")

        restoreStatistic?.let { data ->
            statisticOF.set(data)
            return
        }

        // action
        viewModelScope.launch(Dispatchers.IO) {
            val result = scoreRepository.getStatisticData() { result ->
                CoroutineScope(Dispatchers.Main).launch {
                    // assign restore data
                    restoreStatistic = result
                    // update data to UI
                    statisticOF.set(result)
                }
            }
        }
    }*/
}