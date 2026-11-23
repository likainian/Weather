package com.wallet.weather.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.wallet.weather.bean.Hour
import com.wallet.weather.databinding.ItemWeatherHourlyBinding
import io.iotex.base.bindbase.BaseBindVH

/**
 * 每天的温度列表
 */
class HourlyWeatherAdapter :
    ListAdapter<HourlyWeatherAdapter.Point, BaseBindVH<ItemWeatherHourlyBinding>>(DiffCallback) {

    data class Point(val hour: Hour, val height: Double?, val previous: Double?, val next: Double?)

    fun show(hours: List<Hour>) {
        val values = hours.mapNotNull { it.temperature }
        val minimum = values.minOrNull() ?: 0.0
        val range = ((values.maxOrNull() ?: minimum) - minimum).coerceAtLeast(1.0)
        val heights = hours.map { hour -> hour.temperature?.let { (it - minimum) / range } }
        submitList(hours.mapIndexed { index, hour ->
            Point(hour, heights[index], heights.getOrNull(index - 1), heights.getOrNull(index + 1))
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseBindVH(ItemWeatherHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BaseBindVH<ItemWeatherHourlyBinding>, position: Int) {
        holder.bind.temperatureChart.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<Point>() {
        override fun areItemsTheSame(oldItem: Point, newItem: Point) = oldItem.hour.time == newItem.hour.time
        override fun areContentsTheSame(oldItem: Point, newItem: Point) = oldItem == newItem
    }
}
