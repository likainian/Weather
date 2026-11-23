package com.wallet.weather.api

import com.wallet.weather.bean.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 通过搜索获取城市编码，城市ID
 */

interface GeocodingApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("language") language: String = "zh",
        @Query("count") count: Int = 15
    ): GeocodingResponse
}
