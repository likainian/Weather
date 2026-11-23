package com.wallet.weather.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import kotlin.math.roundToInt

internal object WeatherUtils {
    fun degrees(value: Double?) = value?.let { "${it.roundToInt()}°" } ?: "—"
    fun condition(code: Int) = when (code) {
        0 -> "晴"
        1 -> "大部晴朗"
        2 -> "多云"
        3 -> "阴"
        45, 48 -> "雾"
        51, 53, 55, 56, 57 -> "细雨"
        61, 63, 65, 66, 67 -> "雨"
        71, 73, 75, 77, 85, 86 -> "雪"
        80, 81, 82 -> "阵雨"
        95, 96, 99 -> "雷阵雨"
        else -> "天气未知"
    }

    fun Context.dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
    fun Context.rounded(color: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(18).toFloat()
    }
}
