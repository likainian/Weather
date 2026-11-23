package com.wallet.weather.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * 天气的存储和读取
 */
@Dao
interface WeatherForecastDao {
    @Query("SELECT * FROM forecasts WHERE city_id = :cityId")
    suspend fun forecast(cityId: String): WeatherForecastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(forecast: WeatherForecastEntity)
}
