package com.wallet.weather.bean

import java.io.Serializable

/**
 * 服务端返回城市字段。
 */
data class GeocodingResponse(val results: List<CityBean>?)
data class CityBean(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val admin1: String?,
    val country: String?
)

data class City(val id: String, val name: String, val latitude: Double, val longitude: Double, val region: String = "") : Serializable {
    companion object {
        val beijing = City("1816670", "北京", 39.9042, 116.4074)
    }
}
