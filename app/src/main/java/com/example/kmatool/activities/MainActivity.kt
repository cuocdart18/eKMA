package com.example.kmatool.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.kmatool.R
import com.example.kmatool.base.activities.BaseActivity
import com.example.kmatool.common.AlarmEventsScheduler
import com.example.kmatool.common.Data
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.data.database.AppDatabase
import com.example.kmatool.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUiTemplates()
        setAlarmForLocalEvents()
    }

    private fun setUiTemplates() {
        logDebug("setUiTemplates")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frm_host) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarmForLocalEvents() {
        CoroutineScope(Dispatchers.Default).launch {
            DataStoreManager(application).isNotifyEventsDataStoreFlow.collect() { state ->
                val alarmScheduler = AlarmEventsScheduler(this@MainActivity)
                if (state) {
                    launch {
                        Data.periodsDayMap.forEach { (t, u) ->
                            u.forEach { alarmScheduler.scheduleEvents(it) }
                        }
                    }
                    launch {
                        Data.notesDayMap.forEach { (t, u) ->
                            u.forEach { alarmScheduler.scheduleEvents(it) }
                        }
                    }
                } else {
                    launch {
                        Data.periodsDayMap.forEach { (t, u) ->
                            u.forEach { alarmScheduler.cancel(it) }
                        }
                    }
                    launch {
                        Data.notesDayMap.forEach { (t, u) ->
                            u.forEach { alarmScheduler.cancel(it) }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logDebug("clear AppDatabase")
        AppDatabase.destroyInstance()
    }
}