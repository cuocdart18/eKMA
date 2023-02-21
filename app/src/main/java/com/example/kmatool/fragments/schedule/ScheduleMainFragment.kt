package com.example.kmatool.fragments.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.kmatool.databinding.FragmentScheduleMainBinding
import com.example.kmatool.local.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ScheduleMainFragment : Fragment() {
    private val TAG = ScheduleMainFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleMainBinding
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create $TAG")
        binding = FragmentScheduleMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")

        // test read data
        val dataStoreManager = DataStoreManager(requireContext())
        CoroutineScope(Dispatchers.Main).launch {
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}