package com.example.kmatool.ui.schedule.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentScheduleIntroBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleIntroFragment : BaseFragment() {
    override val TAG = ScheduleIntroFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleIntroBinding
    private val scheduleIntroViewModel by viewModels<ScheduleIntroViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleIntroBinding.inflate(inflater, container, false)
        // handle on click
        binding.btnViewSchedule.setOnClickListener { onClickBtnViewSchedule() }
        return binding.root
    }

    private fun onClickBtnViewSchedule() {
        logDebug("onClickBtnViewSchedule")
        // check login state
        scheduleIntroViewModel.getLoginState(requireContext()) {
            logInfo("callback login state = $it")
        }
    }
}