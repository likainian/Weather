package com.wallet.weather.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 本地存储城市。
 */
@Entity(tableName = "cities")
data class WeatherCityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val region: String
)
