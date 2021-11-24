package com.mint.weather.actualweather.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.mint.weather.model.HourWeather
import com.mint.weather.model.Sunrise
import com.mint.weather.model.Sunset

class HourlyWeatherDiffUtil: DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is HourWeather && newItem is HourWeather -> oldItem.date == newItem.date
            oldItem is Sunrise && newItem is Sunrise -> oldItem.date == newItem.date
            oldItem is Sunset && newItem is Sunset -> oldItem.date == newItem.date
            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is HourWeather && newItem is HourWeather -> oldItem == newItem
            oldItem is Sunrise && newItem is Sunrise -> oldItem == newItem
            oldItem is Sunset && newItem is Sunset -> oldItem == newItem
            else -> false
        }
    }
}
