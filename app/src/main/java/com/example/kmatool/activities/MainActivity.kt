package com.example.kmatool.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.kmatool.R
import com.example.kmatool.base.activities.BaseActivity
import com.example.kmatool.databinding.ActivityMainBinding
import com.example.kmatool.common.TIRAMISU_PERMISSION_REQUEST_CODE
import com.example.kmatool.data.data_source.database.AppDatabase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUiTemplates()
//        requestPostNotifyPermission()
        viewModel.runWorkerIfFailure(this)
    }

    private fun setUiTemplates() {
        logDebug("setUiTemplates")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frm_host) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun requestPostNotifyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                logDebug("Permission POST_NOTIFICATIONS granted")
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    TIRAMISU_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            TIRAMISU_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                }
                return
            }

            else -> {
                logDebug("invalid requestCode = $requestCode")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logDebug("clear AppDatabase")
        AppDatabase.destroyInstance()
    }
}