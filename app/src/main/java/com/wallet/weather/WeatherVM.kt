package com.wallet.weather

import com.wallet.weather.bean.City
import android.app.Application
import com.wallet.weather.api.WeatherApiService
import com.wallet.weather.room.WeatherCityRepository
import com.wallet.weather.room.WeatherForecastRepository
import com.wallet.weather.state.CityWeather
import com.wallet.weather.state.WeatherState
import io.iotex.base.bindbase.BaseLaunchVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class WeatherVM(application: Application) : BaseLaunchVM(application) {
    private val cityRepository = WeatherCityRepository(application)
    private val forecastRepository = WeatherForecastRepository(application)
    private val mutable = MutableStateFlow(WeatherState())
    val state = mutable.asStateFlow()
    private var started = false
    private var locationResolved = false

    fun start() {
        if (started) return
        started = true
        mutable.value = WeatherState(locating = !locationResolved)
        addLaunch(onError = {
            started = false
            mutable.value = WeatherState(failed = true)
        }) {
            val saved = withContext(Dispatchers.IO) { cityRepository.cities() }
            val primary = mutable.value.pages.firstOrNull()?.city ?: City.beijing
            mutable.value = mutable.value.copy(pages = (listOf(primary) + saved).distinctBy { it.id }.map { CityWeather(it) })
            refresh(primary)
        }
    }

    fun finishLocation(city: City?) {
        if (locationResolved) return
        locationResolved = true
        val current = mutable.value
        if (city == null) {
            mutable.value = current.copy(locating = false)
            return
        }
        val pages = current.pages.drop(1).filterNot { it.city.id == city.id }
        val updated = listOf(CityWeather(city)) + pages
        val selectedCity = current.pages.getOrNull(current.selected)?.city
        val selected = updated.indexOfFirst { it.city == selectedCity }.coerceAtLeast(0)
        mutable.value = current.copy(pages = updated, selected = selected, locating = false)
        refresh(city)
    }

    fun select(index: Int) {
        val page = mutable.value.pages.getOrNull(index) ?: return
        mutable.value = mutable.value.copy(selected = index)
        if (page.cache == null && !page.failed && !page.loading) refresh(page.city)
    }

    fun add(city: City, onError: () -> Unit) {
        addLaunch(onError = {
            onError()
        }) {
            withContext(Dispatchers.IO) { cityRepository.add(city) }
            val current = mutable.value
            val pages = (current.pages + CityWeather(city)).distinctBy { it.city.id }
            mutable.value = current.copy(pages = pages)
            select(pages.indexOfFirst { it.city.id == city.id })
        }
    }

    fun refresh(city: City) {
        val page = mutable.value.pages.firstOrNull { it.city == city } ?: return
        if (page.loading) return
        update(city) { it.copy(loading = true, failed = false) }
        addLaunch(onError = {
            update(city) { it.copy(loading = false, failed = true) }
        }) {
            val cached = withContext(Dispatchers.IO) { forecastRepository.read(city) }
            if (cached != null) update(city) { it.copy(cache = cached) }
            val fresh = withContext(Dispatchers.IO) { forecastRepository.save(city, WeatherApiService.forecast(city)) }
            update(city) { it.copy(cache = fresh, loading = false) }
        }
    }

    private fun update(city: City, change: (CityWeather) -> CityWeather) {
        val current = mutable.value
        mutable.value = current.copy(pages = current.pages.map { if (it.city == city) change(it) else it })
    }
}
