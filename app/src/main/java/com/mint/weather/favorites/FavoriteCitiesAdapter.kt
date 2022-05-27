package com.mint.weather.favorites

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mint.weather.R
import com.mint.weather.databinding.ViewFavoritesTextBinding
import com.mint.weather.model.*

class FavoriteCitiesAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(CitiesDiffUtil()) {

    var cityListener: OnCityClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = FavoriteCityView(parent.context)
        val binding2 = ViewFavoritesTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return when (viewType) {
            TYPE_FAVORITES_TEXT -> FavoriteTextViewHolder(binding2)
            TYPE_CITY_CURRENT_WEATHER -> CityCurrentWeatherViewHolder(binding)
            TYPE_CITY_WEATHER -> CityWeatherViewHolder(binding)
            else -> throw java.lang.Exception()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_FAVORITES_TEXT -> (holder as FavoriteTextViewHolder).bind(getItem(position) as FavoritesText)
            TYPE_CITY_CURRENT_WEATHER -> (holder as CityCurrentWeatherViewHolder).bind(getItem(position) as CurrentCityWeather)
            TYPE_CITY_WEATHER -> (holder as CityWeatherViewHolder).bind(getItem(position) as CityWeather)
            else -> throw IllegalStateException("Unexpected value: " + holder.itemViewType)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FavoritesText -> TYPE_FAVORITES_TEXT
            is CurrentCityWeather -> TYPE_CITY_CURRENT_WEATHER
            is CityWeather -> TYPE_CITY_WEATHER
            else -> throw IllegalStateException("Unexpected value")
        }
    }

    inner class FavoriteTextViewHolder(private val binding: ViewFavoritesTextBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritesText: FavoritesText) {
            binding.favoriteText.text = favoritesText.text
        }
    }

    inner class CityCurrentWeatherViewHolder(private val view: FavoriteCityView) : RecyclerView.ViewHolder(view) {
        fun bind(currentCityWeather: CurrentCityWeather) {
            view.setBackgroundColor(Color.parseColor("#76B2FB"))
            view.setCityName(currentCityWeather.city)
            view.setCityWeather(currentCityWeather.currentWeather)
        }
    }

    inner class CityWeatherViewHolder(private val view: FavoriteCityView) : RecyclerView.ViewHolder(view) {

        fun bind(city: CityWeather) {
            view.setBackgroundColor(Color.parseColor("#76B2FB"))
            view.setCityInfo(city)
            view.setOnClickListener {
                cityListener?.onItemClick(city)
            }
        }
    }

    interface OnCityClickListener {
        fun onItemClick(city: CityWeather)
    }

    companion object {
        private const val TYPE_FAVORITES_TEXT = 1
        private const val TYPE_CITY_CURRENT_WEATHER = 2
        private const val TYPE_CITY_WEATHER = 3
    }
}