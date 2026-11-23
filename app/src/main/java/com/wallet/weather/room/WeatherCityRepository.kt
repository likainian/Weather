package com.wallet.weather.room

import android.content.Context
import com.wallet.weather.bean.City

/**
 * 城市读取和存储
 */
internal class WeatherCityRepository(context: Context) {
    private val dao = WeatherDatabase.getInstance(context).weatherCityDao()

    suspend fun cities() = dao.cities().map { City(it.id, it.name, it.latitude, it.longitude, it.region) }

    suspend fun add(city: City) {
        dao.add(WeatherCityEntity(city.id, city.name, city.latitude, city.longitude, city.region))
    }
}
