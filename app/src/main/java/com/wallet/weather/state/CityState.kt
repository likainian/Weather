package com.wallet.weather.state

import com.wallet.weather.bean.City

sealed interface CityState {
    data object Idle : CityState
    data object Loading : CityState
    data object Failed : CityState
    data class Found(val cities: List<City>) : CityState
}
