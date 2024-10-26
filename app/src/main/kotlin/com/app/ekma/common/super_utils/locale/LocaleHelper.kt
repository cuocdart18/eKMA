package com.app.ekma.common.super_utils.locale

import android.content.Context
import com.orhanobut.hawk.Hawk
import java.util.Locale

object LocaleHelper {

    const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun setLocale(context: Context, language: String) {
        saveLanguage(language)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun loadLanguage(context: Context) {
        val language = Hawk.get(SELECTED_LANGUAGE, "")
        setLocale(context, language)
    }

    private fun saveLanguage(language: String) {
        Hawk.put(SELECTED_LANGUAGE, language)
    }
}