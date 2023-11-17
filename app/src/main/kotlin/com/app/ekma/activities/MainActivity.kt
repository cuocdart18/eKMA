package com.app.ekma.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.ekma.R
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.MainBottomNavigation
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeVisible
import com.app.ekma.data.data_source.database.AppDatabase
import com.app.ekma.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // auth
        viewModel.authForUserEntryApp {
            if (it) {
                onAuthSuccessfully()
            } else {
                openActivityWithFinish(LoginActivity::class.java)
            }
        }
    }

    private fun onAuthSuccessfully() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUiTemplates()
        viewModel.firstInitialize(this)
        MainBottomNavigation.setData(false)
    }

    private fun setUiTemplates() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frm_host) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        MainBottomNavigation().observe(this) {
            if (it)
                binding.bottomNav.makeGone()
            else
                binding.bottomNav.makeVisible()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
    }
}