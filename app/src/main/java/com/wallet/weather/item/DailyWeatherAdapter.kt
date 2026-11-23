package com.wallet.weather.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.wallet.weather.R
import com.wallet.weather.bean.Day
import com.wallet.weather.databinding.ItemWeatherDailyBinding
import com.wallet.weather.utils.WeatherUtils.rounded
import io.iotex.base.bindbase.BaseBindVH
import com.wallet.weather.utils.WeatherUtils

/**
 * 15天的温度列表
 */
class DailyWeatherAdapter : ListAdapter<Day, BaseBindVH<ItemWeatherDailyBinding>>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseBindVH(ItemWeatherDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BaseBindVH<ItemWeatherDailyBinding>, position: Int) {
        val day = getItem(position)
        val today = position == 0
        with(holder.bind) {
            root.background = root.context.rounded(
                root.context.getColor(if (today) R.color.weather_day_today else R.color.weather_day_default)
            )
            forecastDate.text = if (today) "今天" else day.date.drop(5)
            weatherCondition.text = WeatherUtils.condition(day.code)
            highTemperature.text = WeatherUtils.degrees(day.high)
            lowTemperature.text = WeatherUtils.degrees(day.low)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Day>() {
        override fun areItemsTheSame(oldItem: Day, newItem: Day) = oldItem.date == newItem.date
        override fun areContentsTheSame(oldItem: Day, newItem: Day) = oldItem == newItem
    }
}
