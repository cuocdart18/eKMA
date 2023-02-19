package com.example.kmatool.fragments.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.kmatool.R
import com.example.kmatool.databinding.FragmentScheduleIntroBinding

class ScheduleIntroFragment : Fragment() {
    private val TAG = ScheduleIntroFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleIntroBinding
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "create view $TAG")
        binding = FragmentScheduleIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")

        navController.navigate(R.id.scheduleLoginFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}