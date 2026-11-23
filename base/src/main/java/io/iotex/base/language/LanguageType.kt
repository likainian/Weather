package io.iotex.base.language

import java.util.*

enum class LanguageType(private val language: String?, private val languageName: String?, private val local: Locale) {

    ENGLISH("en",  "English", Locale("en", "")),
    CHINESE("zh", "中文", Locale("zh", "CN")),
    RUSSIA("ru", "русский", Locale("ru", "")),
    TURKISH("tr", "Türk dili", Locale("tr", "")),
    SPANISH("es",  "Espanol", Locale("es", "")),
    ITALIAN("it",  "lingua italiana", Locale("it", "")),
    DE("de", "Deutsch", Locale("de", "")),
    FR("fr", "française", Locale("fr", "")),
    ID("id",  "bahasa Indonesia", Locale("id", "")),
    MS("ms", "Malaysia", Locale("ms", "")),
    VI("vi", "Việt", Locale("vi", "")),
    KOREAN("ko",  "한국어", Locale("ko", ""));

    fun getLanguage(): String {
        return language ?: ""
    }
    fun getLocal(): Locale {
        return local
    }
    fun getName(): String {
        return languageName ?: ""
    }
}
