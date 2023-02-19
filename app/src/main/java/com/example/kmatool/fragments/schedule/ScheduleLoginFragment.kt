package com.example.kmatool.fragments.schedule

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
import com.example.kmatool.databinding.FragmentScheduleLoginBinding
import com.example.kmatool.utils.md5
import com.example.kmatool.view_model.schedule.ScheduleLoginViewModel

class ScheduleLoginFragment : Fragment() {
    private val TAG = ScheduleLoginFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleLoginBinding
    private val navController: NavController by lazy { findNavController() }
    private val scheduleLoginViewModel: ScheduleLoginViewModel by lazy {
        ViewModelProvider(requireActivity())[ScheduleLoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create view $TAG")
        binding = FragmentScheduleLoginBinding.inflate(inflater, container, false)
        binding.scheduleLoginVM = scheduleLoginViewModel
        // hide text view invalid author
        scheduleLoginViewModel.isValid.set(true)
        // handleOnCLick
        handleOnClick()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
    }

    private fun handleOnClick() {
        Log.d(TAG, "handle on click")
        binding.btnLogin.setOnClickListener { onClickBtnLogin() }
    }

    private fun onClickBtnLogin() {
        val username = binding.edtUsername.text.toString().uppercase()
        val password = md5(binding.edtPassword.text.toString())
        Log.d(TAG, "on click request log in")
        scheduleLoginViewModel.handleOnClickBtnLogin(username, password) {
            handleValidResponseFromApi()
        }
    }

    private fun handleValidResponseFromApi() {
        Log.d(TAG, "handle valid response from Api")
        // hide text view invalid author
        scheduleLoginViewModel.isValid.set(true)
        // action something
        navController.navigate(R.id.scheduleMainFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}