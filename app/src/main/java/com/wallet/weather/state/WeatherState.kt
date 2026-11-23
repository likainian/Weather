package com.wallet.weather.state

import com.wallet.weather.bean.CachedForecast
import com.wallet.weather.bean.City

data class CityWeather(val city: City, val cache: CachedForecast? = null, val loading: Boolean = false, val failed: Boolean = false)
data class WeatherState(
    val pages: List<CityWeather> = emptyList(),
    val selected: Int = 0,
    val locating: Boolean = false,
    val failed: Boolean = false
)
