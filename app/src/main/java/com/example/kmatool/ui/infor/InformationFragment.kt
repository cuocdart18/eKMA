package com.example.kmatool.ui.infor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.kmatool.activities.LoginActivity
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentInformationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment() {
    override val TAG = InformationFragment::class.java.simpleName
    private lateinit var binding: FragmentInformationBinding
    private val viewModel by viewModels<InformationViewModel>()

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
        setUpUI()
    }

    private fun setUpUI() {
        viewModel.getProfile { binding.profile = it }
        viewModel.getImageProfile { setImageUri(it) }

        binding.civProfileImage.setOnClickListener { onClickChangeProfile() }

        binding.btnScore.setOnClickListener {
            showToast("Open my score")
        }

        binding.btnNotifyTrue.setOnClickListener {
            viewModel.storeIsNotifyEvents(true) { showToast("Turn on successfully") }
        }

        binding.btnNotifyFalse.setOnClickListener {
            viewModel.storeIsNotifyEvents(false) { showToast("Turn off successfully") }
        }

        binding.btnSignOut.setOnClickListener { onClickSignOut() }
    }

    private fun onClickSignOut() {
        viewModel.signOut {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun onClickChangeProfile() {
        viewModel.onChangeProfileImage(requireContext()) { uri ->
            setImageUri(uri)
        }
    }

    private fun setImageUri(uri: Uri) {
        binding.civProfileImage.setImageURI(uri)
    }
}