package com.wallet.weather.api

import com.wallet.weather.bean.City
import io.iotex.base.RetrofitClient

/**
 * 通过城市获取天气信息。
 */
internal object WeatherApiService {
    private val forecasts by lazy {
        RetrofitClient.createApiService(
            "https://api.open-meteo.com/",
            WeatherApi::class.java
        )
    }

    suspend fun forecast(city: City) = forecasts.forecast(city.latitude, city.longitude)
}
