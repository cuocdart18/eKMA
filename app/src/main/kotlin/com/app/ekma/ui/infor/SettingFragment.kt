package com.app.ekma.ui.infor

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.app.ekma.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment(
    private val listener: ICallbackOnPreferencesClickListener
) : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences_setting, rootKey)
        setOnClickListenerForPreferences()
    }

    private fun setOnClickListenerForPreferences() {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)

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

        findPreference<Preference>(getString(R.string.key_chat))?.let {
            it.setOnPreferenceClickListener {
                listener.onClickChat()
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

    override fun onSharedPreferenceChanged(sPref: SharedPreferences?, key: String?) {
        if (sPref == null) {
            return
        }
        when (key) {
            getString(R.string.key_notify_event_pref) -> {
                listener.onChangedNotifyEvent(sPref.getBoolean(key, false))
            }

            getString(R.string.key_language_pref) -> {
                listener.onChangedLanguage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    interface ICallbackOnPreferencesClickListener {
        fun onClickMyScore()
        fun onClickUpdateSchedule()
        fun onClickLogOut()
        fun onChangedNotifyEvent(data: Boolean)
        fun onClickChat()
        fun onChangedLanguage()
    }
}