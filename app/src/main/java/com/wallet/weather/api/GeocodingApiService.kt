package com.wallet.weather.api

import com.wallet.weather.bean.City
import io.iotex.base.RetrofitClient

/**
 * 调用城市api，获取城市信息。
 */
internal object GeocodingApiService {
    private val geocoding by lazy {
        RetrofitClient.createApiService(
            "https://geocoding-api.open-meteo.com/",
            GeocodingApi::class.java
        )
    }

    suspend fun search(query: String): List<City> = geocoding.search(query).results.orEmpty().map { bean ->
        val region = listOfNotNull(bean.admin1, bean.country)
            .filter { it.isNotBlank() && it != bean.name }
            .distinct().joinToString(" · ")
        City(bean.id.toString(), bean.name, bean.latitude, bean.longitude, region)
    }
}
