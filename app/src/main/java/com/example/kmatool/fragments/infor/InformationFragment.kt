package com.example.kmatool.fragments.infor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kmatool.databinding.FragmentInformationBinding
import com.example.kmatool.fragments.IntroAppFragment

class InformationFragment : Fragment() {
    private val TAG = IntroAppFragment::class.java.simpleName
    private lateinit var binding: FragmentInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "show $TAG")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "destroy $TAG")
    }
}