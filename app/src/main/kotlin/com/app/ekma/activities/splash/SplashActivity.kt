package com.app.ekma.activities.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ekma.activities.login.LoginActivity
import com.app.ekma.activities.main.MainActivity
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    override val TAG = SplashActivity::class.java.simpleName
    lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(3000L)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}