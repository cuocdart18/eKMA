package com.example.kmatool.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import com.example.kmatool.base.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override val TAG = SplashActivity::class.java.simpleName
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getLoginState {
            if (it) {
                openActivityWithFinish(MainActivity::class.java)
            } else {
                openActivityWithFinish(LoginActivity::class.java)
            }
        }
    }
}