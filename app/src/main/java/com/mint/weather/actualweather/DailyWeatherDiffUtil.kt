package com.mint.weather.actualweather

import androidx.recyclerview.widget.DiffUtil
import com.mint.weather.model.DailyWeatherShort

class DailyWeatherDiffUtil : DiffUtil.ItemCallback<DailyWeatherShort>() {
    override fun areItemsTheSame(oldItem: DailyWeatherShort, newItem: DailyWeatherShort): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyWeatherShort, newItem: DailyWeatherShort): Boolean {
        return oldItem == newItem
    }

}
