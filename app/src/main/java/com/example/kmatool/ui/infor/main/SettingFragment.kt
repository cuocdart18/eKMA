package com.example.kmatool.ui.infor.main

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.kmatool.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment(
    private val listener: ICallbackOnPreferencesClickListener
) : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences_setting, rootKey)
        setOnClickListenerForPreferences()
    }

    private fun setOnClickListenerForPreferences() {
        findPreference<Preference>(getString(R.string.key_my_score_pref))?.let {
            it.setOnPreferenceClickListener {
                listener.onClickMyScore()
                true
            }
        }

        findPreference<Preference>(getString(R.string.key_update_schedule_pref))?.let {
            it.setOnPreferenceClickListener {
                listener.onClickUpdateSchedule()
                true
            }
        }

        findPreference<Preference>(getString(R.string.key_log_out_pref))?.let {
            it.setOnPreferenceClickListener {
                listener.onClickLogOut()
                true
            }
        }
    }

    interface ICallbackOnPreferencesClickListener {
        fun onClickMyScore()
        fun onClickUpdateSchedule()
        fun onClickLogOut()
    }
}