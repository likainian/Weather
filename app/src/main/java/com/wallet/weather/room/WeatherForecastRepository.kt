package com.wallet.weather.room

import android.content.Context
import com.google.gson.Gson
import com.wallet.weather.bean.CachedForecast
import com.wallet.weather.bean.City
import com.wallet.weather.bean.Day
import com.wallet.weather.bean.Forecast
import com.wallet.weather.bean.Hour
import com.wallet.weather.bean.ForecastResponse

/**
 * 天气的存储。
 */
internal class WeatherForecastRepository(context: Context) {
    private val dao = WeatherDatabase.getInstance(context).weatherForecastDao()
    private val gson = Gson()

    suspend fun read(city: City): CachedForecast? = dao.forecast(city.id)?.let {
        val response = gson.fromJson(it.payload, ForecastResponse::class.java)
        CachedForecast(response.toForecast(), it.updatedAt)
    }

    suspend fun save(city: City, response: ForecastResponse): CachedForecast {
        val cached = CachedForecast(response.toForecast(), System.currentTimeMillis())
        dao.save(WeatherForecastEntity(city.id, gson.toJson(response), cached.updatedAt))
        return cached
    }

    private fun ForecastResponse.toForecast(): Forecast {
        val today = current.time.substringBefore('T')
        val hours = hourly.time.mapIndexedNotNull { index, time ->
            val clock = time.substringAfter('T')
            val hour = clock.substringBefore(':').toIntOrNull()
            if (time.substringBefore('T') == today && hour != null && hour % 2 == 0) {
                Hour(clock, hourly.temperature.getOrNull(index))
            } else {
                null
            }
        }
        val days = daily.time.mapIndexed { index, date ->
            Day(
                date,
                daily.weatherCode.getOrNull(index) ?: -1,
                daily.minimumTemperature.getOrNull(index),
                daily.maximumTemperature.getOrNull(index)
            )
        }
        return Forecast(current.temperature, current.weatherCode ?: -1, hours, days)
    }
}
