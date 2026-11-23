package io.iotex.base.language

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import java.util.*


const val SP_LANGUAGE = "sp_language"
const val SP_NAME = "spUtils"
object MultiLanguage {

    fun setLocal(context: Context): Context {
        return updateResources(context, getLocaleLanguage(context))
    }

    private fun setApplicationLanguage(context: Context) {
        val resources = context.applicationContext.resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        val locale = getLocaleLanguage(context)
        config.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            context.applicationContext.createConfigurationContext(config)
            Locale.setDefault(locale)
        }
        resources.updateConfiguration(config, dm)
    }

    fun onConfigurationChanged(context: Context) {
        setLocal(context)
        setApplicationLanguage(context)
    }

    fun getSetLanguageString(context: Context): String {
        return getLocaleLanguage(context).displayLanguage
    }

    fun getSetLanguageType(context: Context): LanguageType {
        val local = getLocaleLanguage(context)
        for (index in LanguageType.entries) {
            if (index.getLocal() == (local)) {
                return index
            }
        }
        return LanguageType.ENGLISH
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        var contextValue = context
        Locale.setDefault(locale)
        val res = contextValue.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            contextValue = contextValue.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return contextValue
    }

    private fun getLocaleLanguage(context: Context): Locale {
        val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val language = sharedPreferences.getString(SP_LANGUAGE, "")
        val locale: Locale
        var languageDesc = language
        if (TextUtils.isEmpty(languageDesc)) {
            locale = getSystemLocal()
            languageDesc = locale.language
            sharedPreferences.edit().putString(SP_LANGUAGE, languageDesc).apply()
        }
        for (index in LanguageType.entries) {
            if (index.getLanguage() == languageDesc) {
                return index.getLocal()
            }
        }
        return Locale.ENGLISH
    }

    private fun getSystemLocal(): Locale {
        val locale = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Locale.getDefault()
        } else {
            LocaleList.getDefault().get(0)
        }
        return locale
    }
}
