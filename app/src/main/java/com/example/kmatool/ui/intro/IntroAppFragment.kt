package com.example.kmatool.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentAppIntroBinding

class IntroAppFragment : BaseFragment() {
    override val TAG = IntroAppFragment::class.java.simpleName
    private lateinit var binding: FragmentAppIntroBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppIntroBinding.inflate(inflater, container, false)
        return binding.root
    }
}