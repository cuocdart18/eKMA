package com.example.kmatool.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.kmatool.databinding.FragmentAppIntroBinding

class IntroAppFragment : Fragment() {
    private val TAG = IntroAppFragment::class.java.simpleName
    private lateinit var binding: FragmentAppIntroBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create view $TAG")
        binding = FragmentAppIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
        navController = findNavController()
    }

    override fun onDestroy() {
        Log.d(TAG, "destroy $TAG")
        super.onDestroy()
    }
}