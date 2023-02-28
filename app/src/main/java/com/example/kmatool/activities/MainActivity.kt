package com.example.kmatool.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.kmatool.R
import com.example.kmatool.database.AppDatabase
import com.example.kmatool.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "on create view $TAG")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUiTemplates()
    }

    private fun setUiTemplates() {
        Log.d(TAG, "set UI template")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.frm_host) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "on destroy $TAG")
        // clear database ref
        Log.d(TAG, "clear AppDatabase")
        AppDatabase.destroyInstance()
    }
}