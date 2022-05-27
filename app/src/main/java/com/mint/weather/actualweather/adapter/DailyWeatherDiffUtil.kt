package com.mint.weather.actualweather.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mint.weather.model.DailyWeather

class DailyWeatherDiffUtil : DiffUtil.ItemCallback<DailyWeather>() {
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem == newItem
    }

}
