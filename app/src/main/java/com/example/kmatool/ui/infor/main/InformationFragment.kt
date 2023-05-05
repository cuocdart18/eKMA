package com.example.kmatool.ui.infor.main

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.example.kmatool.R
import com.example.kmatool.activities.LoginActivity
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentInformationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener,
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

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sPref: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.key_notify_event_pref) -> {
                onChangedNotifyEvent(sPref!!.getBoolean(key, false))
            }

            getString(R.string.key_dark_mode_pref) -> {
                logDebug("on click dark mode value=${sPref?.getBoolean(key, false)}")
            }

            getString(R.string.key_language_pref) -> {

            }
        }
    }

    override fun onClickMyScore() {
        showToast("on click open my score fragment")
    }

    override fun onClickUpdateSchedule() {
        showToast("on click update schedule")
    }

    override fun onClickLogOut() {
        viewModel.signOut { onClickSignOut() }
    }

    private fun onChangedNotifyEvent(data: Boolean) {
        viewModel.storeIsNotifyEvents(data) {
            logDebug("save notify successfully")
        }
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

    private fun setImageUri(uri: Uri) {
        binding.civProfileImage.setImageURI(uri)
    }

    private fun onClickSignOut() {
        viewModel.signOut {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}