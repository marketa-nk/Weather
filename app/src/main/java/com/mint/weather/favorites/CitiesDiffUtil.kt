package com.mint.weather.favorites

import androidx.recyclerview.widget.DiffUtil
import com.mint.weather.model.CityWeatherLong

class CitiesDiffUtil : DiffUtil.ItemCallback<CityWeatherLong>() {
    override fun areItemsTheSame(oldItem: CityWeatherLong, newItem: CityWeatherLong): Boolean {
        return oldItem.cityId == newItem.cityId
    }

    override fun areContentsTheSame(oldItem: CityWeatherLong, newItem: CityWeatherLong): Boolean {
        return oldItem == newItem
    }

}
