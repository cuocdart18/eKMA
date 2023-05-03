package com.example.kmatool.ui.infor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.kmatool.activities.LoginActivity
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.DataStoreManager
import com.example.kmatool.databinding.FragmentInformationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InformationFragment : BaseFragment() {
    override val TAG = InformationFragment::class.java.simpleName
    private lateinit var binding: FragmentInformationBinding
    private val viewModel by viewModels<InformationViewModel>()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

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

        viewModel.profile.observe(viewLifecycleOwner) {
            binding.profile = it
        }

        binding.btnScore.setOnClickListener {
            showToast("Open my score")
        }

        binding.btnNotifyTrue.setOnClickListener {
            viewModel.storeIsNotifyEvents(true) { showToast("Turn on successfully") }
        }

        binding.btnNotifyFalse.setOnClickListener {
            viewModel.storeIsNotifyEvents(false) { showToast("Turn off successfully") }
        }

        binding.btnSignOut.setOnClickListener {
            viewModel.signOut {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}