package com.wallet.weather.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 本地存储天气
 */
@Entity(tableName = "forecasts")
data class WeatherForecastEntity(
    @PrimaryKey @ColumnInfo(name = "city_id") val cityId: String,
    val payload: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
