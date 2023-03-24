package com.example.kmatool.ui.schedule.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentScheduleLoginBinding
import com.example.kmatool.utils.md5
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable

class ScheduleLoginFragment : BaseFragment() {
    override val TAG = ScheduleLoginFragment::class.java.simpleName
    private lateinit var binding: FragmentScheduleLoginBinding
    private val scheduleLoginViewModel: ScheduleLoginViewModel by lazy {
        ViewModelProvider(requireActivity())[ScheduleLoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleLoginBinding.inflate(inflater, container, false)
        binding.scheduleLoginVM = scheduleLoginViewModel
        // setup google progress
        setupGoogleProgress()
        // handleOnCLick
        handleOnClick()
        return binding.root
    }

    private fun setupGoogleProgress() {
        binding.googleProgress.indeterminateDrawable =
            ChromeFloatingCirclesDrawable.Builder(requireContext())
                .colors(resources.getIntArray(R.array.google_colors))
                .build()
    }

    private fun handleOnClick() {
        logDebug("handleOnClick")
        binding.btnLogin.setOnClickListener { onClickBtnLogin() }
    }

    private fun onClickBtnLogin() {
        val username = binding.edtUsername.text.toString().uppercase()
        val password = md5(binding.edtPassword.text.toString())
        logDebug("onClickBtnLogin")
        scheduleLoginViewModel.handleOnClickBtnLogin(
            requireContext().applicationContext,
            username,
            password
        ) {
            handleValidResponseFromApi()
        }
    }

    private fun handleValidResponseFromApi() {
        logDebug("handleValidResponseFromApi")
    }
}