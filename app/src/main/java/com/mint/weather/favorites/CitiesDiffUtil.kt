package com.mint.weather.favorites

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.mint.weather.model.*

class CitiesDiffUtil : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is FavoritesText && newItem is FavoritesText -> oldItem.text == newItem.text
            oldItem is CurrentCityWeather && newItem is CurrentCityWeather -> oldItem.city == newItem.city
            oldItem is CityWeather && newItem is CityWeather -> oldItem.city.id == newItem.city.id
            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is FavoritesText && newItem is FavoritesText -> oldItem == newItem
            oldItem is CurrentCityWeather && newItem is CurrentCityWeather -> oldItem == newItem
            oldItem is CityWeather && newItem is CityWeather -> oldItem == newItem
            else -> false
        }
    }

}
