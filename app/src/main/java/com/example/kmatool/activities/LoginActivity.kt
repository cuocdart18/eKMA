package com.example.kmatool.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kmatool.R
import com.example.kmatool.databinding.ActivityLoginBinding
import com.example.kmatool.utils.md5
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "on create $TAG")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        // setup google progress
        setupGoogleProgress()
        // handleOnCLick
        handleOnClick()
        // check login state
        viewModel.getLoginState(this) {
            // if login state = true
            openMainActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "on start $TAG")
    }

    private fun setupGoogleProgress() {
        binding.googleProgress.indeterminateDrawable =
            ChromeFloatingCirclesDrawable.Builder(this)
                .colors(resources.getIntArray(R.array.google_colors))
                .build()
    }

    private fun handleOnClick() {
        Log.d(TAG, "handle on click")
        binding.btnLogin.setOnClickListener { onClickBtnLogin() }
    }

    private fun onClickBtnLogin() {
        val username = binding.edtUsername.text.toString().uppercase()
        val password = md5(binding.edtPassword.text.toString())
        Log.d(TAG, "on click request log in")
        viewModel.handleOnClickBtnLogin(
            this,
            username,
            password
        ) {
            // if login successfully
            openMainActivity()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "on destroy $TAG")
    }
}