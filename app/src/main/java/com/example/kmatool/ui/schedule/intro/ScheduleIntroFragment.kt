package com.example.kmatool.ui.schedule.intro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.kmatool.R
import com.example.kmatool.databinding.FragmentScheduleIntroBinding

class ScheduleIntroFragment : Fragment() {
    private val TAG = ScheduleIntroFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleIntroBinding
    private val navController: NavController by lazy { findNavController() }
    private val scheduleIntroViewModel: ScheduleIntroViewModel by lazy {
        ViewModelProvider(requireActivity())[ScheduleIntroViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create view $TAG")
        binding = FragmentScheduleIntroBinding.inflate(inflater, container, false)
        // handle on click
        binding.btnViewSchedule.setOnClickListener { onClickBtnViewSchedule() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
    }

    private fun onClickBtnViewSchedule() {
        Log.d(TAG, "on click btn view schedule")
        // action
        // check login state
        scheduleIntroViewModel.getLoginState(requireContext()) {
            Log.i(TAG, "callback login state = $it")
            /*// navigate
            if (it) {
                navController.navigate(R.id.scheduleMainFragment)
            } else {
                navController.navigate(R.id.scheduleLoginFragment)
            }*/
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}