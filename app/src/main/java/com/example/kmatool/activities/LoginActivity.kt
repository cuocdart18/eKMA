package com.example.kmatool.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.kmatool.R
import com.example.kmatool.base.activities.BaseActivity
import com.example.kmatool.databinding.ActivityLoginBinding
import com.example.kmatool.utils.md5
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    override val TAG = LoginActivity::class.java.simpleName
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel

        setupGoogleProgress(binding.googleProgress)
        handleOnClick()
        // check login state
        viewModel.getLoginState {
            // get local data if already logged in
            viewModel.getLocalData {
                // if login successfully
                openMainActivity()
            }
        }
    }

    private fun handleOnClick() {
        binding.btnLogin.setOnClickListener { onClickBtnLogin() }
    }

    private fun onClickBtnLogin() {
        logDebug("on click request log in")
        val username = binding.edtUsername.text.toString().uppercase()
        val password = md5(binding.edtPassword.text.toString())
        viewModel.handleOnClickBtnLogin(username, password) {
            // get local data if already logged in
            viewModel.getLocalData {
                // if login successfully
                openMainActivity()
            }
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}