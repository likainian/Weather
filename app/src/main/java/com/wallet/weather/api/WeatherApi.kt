package com.wallet.weather.api

import com.wallet.weather.bean.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 通过城市位置获取天气情况。
 */
interface WeatherApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 15,
        @Query("current") current: String = "temperature_2m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m",
        @Query("daily") daily: String = "weather_code,temperature_2m_min,temperature_2m_max"
    ): ForecastResponse
}
