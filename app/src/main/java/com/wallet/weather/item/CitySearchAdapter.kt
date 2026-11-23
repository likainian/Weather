package com.wallet.weather.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.wallet.weather.bean.City
import com.wallet.weather.databinding.ItemWeatherCityBinding
import io.iotex.base.bindbase.BaseBindVH

class CitySearchAdapter(private val onCitySelected: (City) -> Unit) :
    ListAdapter<City, BaseBindVH<ItemWeatherCityBinding>>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseBindVH(ItemWeatherCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BaseBindVH<ItemWeatherCityBinding>, position: Int) {
        val city = getItem(position)
        with(holder.bind) {
            cityName.text = city.name
            cityRegion.text = city.region
            cityRegion.isVisible = city.region.isNotBlank()
            root.setOnClickListener { onCitySelected(city) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: City, newItem: City) = oldItem == newItem
    }
}
