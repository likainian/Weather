package com.wallet.weather

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wallet.weather.databinding.FragmentWeatherPageBinding
import com.wallet.weather.item.DailyWeatherAdapter
import com.wallet.weather.item.HourlyWeatherAdapter
import io.iotex.base.bindbase.BaseBindFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.wallet.weather.utils.WeatherUtils
import com.wallet.weather.state.CityWeather

class WeatherPageFragment :
    BaseBindFragment<WeatherVM, FragmentWeatherPageBinding>(R.layout.fragment_weather_page) {

    private val weatherVM by lazy { ViewModelProvider(requireActivity())[WeatherVM::class.java] }
    private val hourlyAdapter = HourlyWeatherAdapter()
    private val dailyAdapter = DailyWeatherAdapter()

    internal val cityId: String
        get() = requireNotNull(requireArguments().getString(ARG_CITY_ID))

    override fun initView() {
        with(mBinding) {
            hourlyList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            hourlyList.adapter = hourlyAdapter
            dailyList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            dailyList.adapter = dailyAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.lifecycleOwner = viewLifecycleOwner
        val id = cityId
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherVM.state
                    .map { state -> state.pages.firstOrNull { it.city.id == id } }
                    .distinctUntilChanged()
                    .collect(::render)
            }
        }
    }

    override fun onDestroyView() {
        mBinding.lifecycleOwner = null
        super.onDestroyView()
    }

    private fun render(page: CityWeather?) = with(mBinding) {
        val forecast = page?.cache?.forecast
        pageStatus.isVisible = forecast == null
        forecastContent.isVisible = forecast != null
        if (forecast == null) {
            pageStatus.text = if (page?.failed == true) "暂时无法获取天气" else "正在获取天气…"
            return@with
        }

        currentTemperature.text = WeatherUtils.degrees(forecast.temperature)
        currentCondition.text = WeatherUtils.condition(forecast.code)
        val today = forecast.days.firstOrNull()
        currentRange.isVisible = today != null
        currentRange.text = today?.let { "最高 ${WeatherUtils.degrees(it.high)}    最低 ${WeatherUtils.degrees(it.low)}" }

        val hasHours = forecast.hours.any { it.temperature != null }
        hourlyList.isVisible = hasHours
        hourlyEmpty.isVisible = !hasHours
        hourlyAdapter.show(forecast.hours)
        dailyAdapter.submitList(forecast.days)
    }

    companion object {
        private const val ARG_CITY_ID = "city_id"

        fun newInstance(cityId: String) = WeatherPageFragment().apply {
            arguments = bundleOf(ARG_CITY_ID to cityId)
        }
    }
}
