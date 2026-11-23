package com.wallet.weather.bean

import com.google.gson.annotations.SerializedName

/**
 * 服务返回天气信心
 */
data class ForecastResponse(
    val current: CurrentWeatherBean,
    val hourly: HourlyWeatherBean,
    val daily: DailyWeatherBean
)

data class CurrentWeatherBean(
    val time: String,
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("weather_code") val weatherCode: Int?
)

data class DailyWeatherBean(
    val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int?>,
    @SerializedName("temperature_2m_min") val minimumTemperature: List<Double?>,
    @SerializedName("temperature_2m_max") val maximumTemperature: List<Double?>
)

data class HourlyWeatherBean(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double?>
)

data class Hour(val time: String, val temperature: Double?)
data class Day(val date: String, val code: Int, val low: Double?, val high: Double?)
data class Forecast(val temperature: Double?, val code: Int, val hours: List<Hour>, val days: List<Day>)
data class CachedForecast(val forecast: Forecast, val updatedAt: Long)
