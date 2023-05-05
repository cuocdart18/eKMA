package com.example.kmatool.ui.infor.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.kmatool.R
import com.example.kmatool.activities.LoginActivity
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.KEY_PASS_MINISTUDENT_ID
import com.example.kmatool.databinding.FragmentInformationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment(),
    SettingFragment.ICallbackOnPreferencesClickListener {
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
        setUpPreferencesSetting()
        setUpProfile()
    }

    private fun setUpPreferencesSetting() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frm_container, SettingFragment(this))
            .commit()
    }

    private fun setUpProfile() {
        viewModel.getProfile { binding.profile = it }
        viewModel.getImageProfile { setImageUri(it) }
        binding.civProfileImage.setOnClickListener { onClickChangeProfile() }
    }

    private fun onClickChangeProfile() {
        viewModel.onChangeProfileImage(requireContext()) { uri ->
            setImageUri(uri)
        }
    }

    override fun onClickMyScore() {
        logDebug("on click open my score fragment")
        viewModel.getProfile {
            val id = it.studentCode
            logDebug("navigate detail fragment with student = $id")
            // action
            val bundle = bundleOf(
                KEY_PASS_MINISTUDENT_ID to id
            )
            navigateToFragment(R.id.studentDetailFragment, bundle)
        }
    }

    override fun onClickUpdateSchedule() {
        logDebug("on click update schedule")
    }

    override fun onChangedNotifyEvent(data: Boolean) {
        viewModel.changedIsNotifyEvents(data) { }
    }

    override fun onChangedDarkMode(data: Boolean) {
        logDebug("changed dark mode data=$data")
    }

    override fun onChangedLanguage() {
        logDebug("changed language")
    }

    override fun onClickLogOut() {
        viewModel.signOut() {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun setImageUri(uri: Uri) {
        binding.civProfileImage.setImageURI(uri)
    }
}