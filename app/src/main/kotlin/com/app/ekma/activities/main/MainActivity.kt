package com.app.ekma.activities.main

import android.os.Bundle
import androidx.activity.viewModels
import com.app.ekma.R
import com.app.ekma.activities.login.LoginActivity
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.INFORMATION_FRAGMENT
import com.app.ekma.common.NOTE_FRAGMENT
import com.app.ekma.common.SCHEDULE_FRAGMENT
import com.app.ekma.common.SCORE_FRAGMENT
import com.app.ekma.common.pattern.singleton.MainBottomNavigation
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeVisible
import com.app.ekma.data.data_source.database.AppDatabase
import com.app.ekma.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private val viewPagerAdapter by lazy { MainViewPagerAdapter(this) }
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
        viewModel.firstInitialize(this)
        setUiTemplates()
        MainBottomNavigation.setData(false)
    }

    private fun setUiTemplates() {
        setUpHostFragment()
        MainBottomNavigation().observe(this) {
            if (it)
                binding.bottomNav.makeGone()
            else
                binding.bottomNav.makeVisible()
        }
    }

    private fun setUpHostFragment() {
        binding.frmHost.adapter = viewPagerAdapter
        binding.frmHost.offscreenPageLimit = 3
        binding.frmHost.isUserInputEnabled = false
        // for bottom navigation view
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.score -> {
                    binding.frmHost.setCurrentItem(SCORE_FRAGMENT, false)
                }

                R.id.schedule -> {
                    binding.frmHost.setCurrentItem(SCHEDULE_FRAGMENT, false)
                }

                R.id.note -> {
                    binding.frmHost.setCurrentItem(NOTE_FRAGMENT, false)
                }

                R.id.information -> {
                    binding.frmHost.setCurrentItem(INFORMATION_FRAGMENT, false)
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
    }
}