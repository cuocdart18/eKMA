package com.example.kmatool.ui.infor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.databinding.FragmentInformationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InformationFragment : BaseFragment() {
    override val TAG = InformationFragment::class.java.simpleName
    private lateinit var binding: FragmentInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNotifyTrue.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                DataStoreManager(requireActivity().application).storeIsNotifyEvents(true)
            }
        }

        binding.btnNotifyFalse.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                DataStoreManager(requireActivity().application).storeIsNotifyEvents(false)
            }
        }
    }
}