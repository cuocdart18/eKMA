package com.example.kmatool.activities

import android.os.Bundle
import android.text.Html
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.example.kmatool.R
import com.example.kmatool.base.activities.BaseActivity
import com.example.kmatool.databinding.ActivityLoginBinding
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
        setUpUI()
    }

    private fun setUpUI() {
        binding.btnLogin.setOnClickListener { onClickBtnLogin() }
        binding.tvStartedTitle.text = Html.fromHtml(getString(R.string.log_in_title_app))
        binding.edtUsername.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty())
                binding.tilUsername.error = " "
            else
                binding.tilUsername.error = ""
        }
        binding.edtPassword.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty())
                binding.tilPassword.error = " "
            else
                binding.tilPassword.error = ""
        }
    }

    private fun onClickBtnLogin() {
        val username = binding.edtUsername.text.toString().uppercase()
        val unHashedPassword = binding.edtPassword.text.toString()

        showErrorIfBlankField(username, unHashedPassword)

        viewModel.handleOnClickBtnLogin(username, unHashedPassword) {
            // if login successfully
            openActivityWithFinish(MainActivity::class.java)
        }
    }

    private fun showErrorIfBlankField(username: String, unHashedPassword: String) {
        if (username.isBlank()) {
            binding.tilUsername.error = " "
            binding.edtUsername.clearFocus()
        }
        if (unHashedPassword.isBlank()) {
            binding.tilPassword.error = " "
            binding.edtPassword.clearFocus()
        }
    }
}