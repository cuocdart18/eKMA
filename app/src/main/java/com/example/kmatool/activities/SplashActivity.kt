package com.example.kmatool.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import com.example.kmatool.base.activities.BaseActivity
import com.example.kmatool.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override val TAG = SplashActivity::class.java.simpleName
    lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getLoginState {
            if (it) {
                openActivityWithFinish(MainActivity::class.java)
            } else {
                openActivityWithFinish(LoginActivity::class.java)
            }
        }
    }
}