package com.app.ekma.ui.score.subject

import android.os.Bundle
import android.view.*
import com.app.ekma.base.dialogs.BaseDialogFragment
import com.app.ekma.common.KEY_PASS_STATISTIC_SUBJECT
import com.app.ekma.data.models.StatisticSubject
import com.app.ekma.databinding.DialogStatisticSubjectBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticSubjectDialogFragment : BaseDialogFragment<DialogStatisticSubjectBinding>() {
    override val TAG = StatisticSubjectDialogFragment::class.java.simpleName
    private lateinit var statisticSubject: StatisticSubject

    override fun getDataBinding() = DialogStatisticSubjectBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiveDataFromStudentDetailFragment()
        showStatisticSubjectDataToUI()
    }

    private fun receiveDataFromStudentDetailFragment() {
        val bundle = arguments
        bundle?.let {
            statisticSubject =
                it.get(KEY_PASS_STATISTIC_SUBJECT) as StatisticSubject
        }
        logInfo("receive statistic subject = $statisticSubject")
    }

    private fun showStatisticSubjectDataToUI() {
        logDebug("showStatisticSubjectDataToUI")
        // action
        binding.statisticSubject = statisticSubject
    }
}