package com.example.kmatool.fragments.infor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kmatool.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {
    private val TAG = InformationFragment::class.java.simpleName
    private lateinit var binding: FragmentInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create view $TAG")
        binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}