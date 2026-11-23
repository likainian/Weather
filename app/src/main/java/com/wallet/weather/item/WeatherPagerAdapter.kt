package com.wallet.weather.item

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wallet.weather.state.CityWeather
import com.wallet.weather.WeatherPageFragment
import kotlin.math.roundToLong

/**
 * 城市切换页适配器
 */
class WeatherPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private var cityIds = emptyList<String>()
    private var itemIds = emptyList<Long>()
    private var activeIds = emptySet<Long>()

    fun show(pages: List<CityWeather>) {
        val next = pages.map { it.city.id }
        if (next == cityIds) return
        cityIds = next
        itemIds = next.map(::stableId)
        activeIds = itemIds.toSet()
        notifyDataSetChanged()
    }

    override fun getItemCount() = cityIds.size

    override fun getItemId(position: Int) = itemIds[position]

    override fun containsItem(itemId: Long) = itemId in activeIds

    override fun createFragment(position: Int) = WeatherPageFragment.newInstance(cityIds[position])

    private fun stableId(cityId: String): Long {
        cityId.toLongOrNull()?.let { return it }
        // Location IDs contain coordinates rounded to two decimal places.
        val (_, latitude, longitude) = cityId.split(':')
        val lat = (latitude.toDouble() * 100).roundToLong() + 9000
        val lon = (longitude.toDouble() * 100).roundToLong() + 18000
        return -2 - (lat * 36001 + lon)
    }
}
