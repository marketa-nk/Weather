package com.mint.weather.favorites

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mint.weather.model.CityWeatherLong

class FavoriteCitiesAdapter : ListAdapter<CityWeatherLong, FavoriteCitiesAdapter.CityViewHolder>(CitiesDiffUtil()) {

    var cityListener: OnCityClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = FavoriteCityView(parent.context)

        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CityViewHolder(private val view: FavoriteCityView) : RecyclerView.ViewHolder(view) {

        fun bind(city: CityWeatherLong) {
            view.setBackgroundColor(Color.parseColor("#76B2FB"))
            view.setCityInfo(city)
            view.setOnClickListener {
                cityListener?.onItemClick(city)
            }
        }
    }

    interface OnCityClickListener {
        fun onItemClick(city: CityWeatherLong)
    }
}