package com.example.kmatool.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.kmatool.R
import com.example.kmatool.base.activities.BaseActivity
import com.example.kmatool.databinding.ActivityLoginBinding
import com.example.kmatool.utils.md5
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable

class LoginActivity : BaseActivity() {
    override val TAG = LoginActivity::class.java.simpleName
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun setupGoogleProgress() {
        binding.googleProgress.indeterminateDrawable =
            ChromeFloatingCirclesDrawable.Builder(this)
                .colors(resources.getIntArray(R.array.google_colors))
                .build()
    }

    private fun handleOnClick() {
        logDebug("handle on click")
        binding.btnLogin.setOnClickListener { onClickBtnLogin() }
    }

    private fun onClickBtnLogin() {
        logDebug("on click request log in")
        val username = binding.edtUsername.text.toString().uppercase()
        val password = md5(binding.edtPassword.text.toString())
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
}