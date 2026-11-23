package com.wallet.weather

import android.app.Application
import com.wallet.weather.api.GeocodingApiService
import com.wallet.weather.state.CityState
import io.iotex.base.bindbase.BaseLaunchVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CitySearchVM(application: Application) : BaseLaunchVM(application) {
    private val mutableResult = MutableStateFlow<CityState>(CityState.Idle)
    val result = mutableResult.asStateFlow()
    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        mutableResult.value = CityState.Loading
        addLaunchSingle("city", onError = {
            mutableResult.value = CityState.Failed
        }) {
            mutableResult.value = CityState.Found(GeocodingApiService.search(query))
        }
    }
}
