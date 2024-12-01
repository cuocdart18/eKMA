package com.app.ekma.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.common.INFORMATION_FRAGMENT
import com.app.ekma.common.NOTE_FRAGMENT
import com.app.ekma.common.SCHEDULE_FRAGMENT
import com.app.ekma.common.SCORE_FRAGMENT
import com.app.ekma.common.custom_view.ItemBottomView.ACCOUNT
import com.app.ekma.common.custom_view.ItemBottomView.NOTE
import com.app.ekma.common.custom_view.ItemBottomView.SCHEDULE
import com.app.ekma.common.custom_view.ItemBottomView.SCORE
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeVisible
import com.app.ekma.common.pattern.singleton.MainBottomNavigation
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.click.MoveByTouchListener
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.data.data_source.database.AppDatabase
import com.app.ekma.databinding.ActivityMainBinding
import com.app.ekma.ui.chat.activity.ChatActivity
import com.app.ekma.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private val viewPagerAdapter by lazy { MainViewPagerAdapter(this) }
    private val viewModel by viewModels<MainViewModel>()

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

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
        setContentView(binding.root)
        viewModel.firstInitialize(this)
        setUiTemplates()
        MainBottomNavigation.setData(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUiTemplates() {
        binding.bottomNav.setInitItem()
        setUpHostFragment()
        MainBottomNavigation().observe(this) {
            if (it)
                binding.bottomNav.makeGone()
            else
                binding.bottomNav.makeVisible()
        }

        binding.chatShortcut.visible(true)
        binding.chatShortcut.performClick {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        val rootView = findViewById<View>(android.R.id.content)
        rootView.post {
            val parentWidth = binding.layoutRoot.width
            val parentHeight = binding.layoutRoot.height

            // Áp dụng listener vào View
            binding.chatShortcut.setOnTouchListener(MoveByTouchListener(parentWidth, parentHeight))
        }
    }

    private fun setUpHostFragment() {
        binding.frmHost.adapter = viewPagerAdapter
        binding.frmHost.offscreenPageLimit = 3
        binding.frmHost.isUserInputEnabled = false
        // for bottom navigation view
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item) {
                SCORE -> binding.frmHost.setCurrentItem(SCORE_FRAGMENT, false)
                SCHEDULE -> binding.frmHost.setCurrentItem(SCHEDULE_FRAGMENT, false)
                NOTE -> binding.frmHost.setCurrentItem(NOTE_FRAGMENT, false)
                ACCOUNT -> binding.frmHost.setCurrentItem(INFORMATION_FRAGMENT, false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (this::binding.isInitialized) runCatching {
            binding.chatShortcut.visible(true)
        }
    }

    override fun onStop() {
        super.onStop()
        runCatching {
            binding.chatShortcut.gone(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
    }
}