package com.wallet.weather.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * 城市读取和本地存储。
 */
@Dao
interface WeatherCityDao {
    @Query("SELECT * FROM cities ORDER BY rowid")
    suspend fun cities(): List<WeatherCityEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(city: WeatherCityEntity)
}
