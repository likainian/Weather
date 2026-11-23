package com.wallet.weather.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object TimeUtils {
    fun formatUpdateTime(timestamp: Long): String =
        SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp))
}
