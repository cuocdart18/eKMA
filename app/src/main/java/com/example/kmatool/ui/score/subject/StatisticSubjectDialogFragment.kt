package com.example.kmatool.ui.score.subject

import android.os.Bundle
import android.view.*
import com.example.kmatool.base.dialogs.BaseDialogFragment
import com.example.kmatool.databinding.DialogStatisticSubjectBinding
import com.example.kmatool.data.models.StatisticSubject
import com.example.kmatool.utils.KEY_PASS_STATISTIC_SUBJECT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticSubjectDialogFragment : BaseDialogFragment() {
    override val TAG = StatisticSubjectDialogFragment::class.java.simpleName
    private lateinit var binding: DialogStatisticSubjectBinding
    private lateinit var statisticSubject: StatisticSubject

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogStatisticSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // receive data from main fragment
        receiveDataFromStudentDetailFragment()
        // show data to UI
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