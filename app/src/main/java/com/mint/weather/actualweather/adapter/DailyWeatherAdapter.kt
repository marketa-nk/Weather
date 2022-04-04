package com.mint.weather.actualweather.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mint.weather.R
import com.mint.weather.databinding.ViewDailyWeatherBinding
import com.mint.weather.model.DailyWeatherShort
import java.text.SimpleDateFormat

class DailyWeatherAdapter : ListAdapter<DailyWeatherShort, DailyWeatherAdapter.DailyWeatherViewHolder>(DailyWeatherDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val binding = ViewDailyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DailyWeatherViewHolder(private val binding: ViewDailyWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(dailyWeatherShort: DailyWeatherShort) {

            binding.dayOfWeek.text = SimpleDateFormat("EEE").format(dailyWeatherShort.date)
            binding.date.text = SimpleDateFormat("d MMMM").format(dailyWeatherShort.date)
            binding.tempDay.text = temperatureToString(dailyWeatherShort.tempDay)
            binding.tempNight.text = temperatureToString(dailyWeatherShort.tempNight)
            binding.precipitation.text = setPrecipitationVolume(dailyWeatherShort.rain, dailyWeatherShort.snow)
            binding.precipitation.setPrecipitationDrawables(dailyWeatherShort.tempDay)

            binding.windSpeed.setCompoundDrawablesWithIntrinsicBounds(0, 0, dailyWeatherShort.windDirection.directionIcon, 0)
            binding.windSpeed.text = setWindSpeed(dailyWeatherShort.windSpeed, dailyWeatherShort.windGust)
            if (dailyWeatherShort.windGust > 15) {
                binding.windSpeed.setTextColor(ResourcesCompat.getColor(binding.root.resources, R.color.orange_600, binding.root.context.theme))
            } else {
                binding.windSpeed.setTextColor(binding.root.context.getColorResCompat(android.R.attr.textColorSecondary))
            }

            Glide
                .with(binding.root)
                .load("https://openweathermap.org/img/wn/${dailyWeatherShort.icon}@2x.png")
                .into(binding.icon)
        }

        private fun setWindSpeed(windSpeed: Double, windGust: Double): String {
            return when {
                windGust > 15 -> "${windSpeed.toInt()}-${windGust.toInt()}м/с "
                else -> "${windSpeed.toInt()}м/с "
            }
        }
    }

    @ColorInt
    @SuppressLint("ResourceAsColor")
    fun Context.getColorResCompat(@AttrRes id: Int): Int {
        val resolvedAttr = TypedValue()
        theme.resolveAttribute(id, resolvedAttr, true)
        val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
        return ContextCompat.getColor(this, colorRes)
    }

    fun setPrecipitationVolume(rain: Double?, snow: Double?): String {
        return when {
            rain != null && rain > 0 -> "${kotlin.math.ceil(rain).toInt()}мм "
            snow != null && snow > 0 -> "${kotlin.math.ceil(snow).toInt()}мм "
            else -> "— "
        }
    }

    fun temperatureToString(temp: Double): String {
        return when {
            temp >= 1 -> "+${temp.toInt()}°C"
            else -> "${temp.toInt()}°C"
        }
    }
}

private fun TextView.setPrecipitationDrawables(tempDay: Double) {
    if (tempDay >= 0) {
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_water_drop_24, 0)
    }
    else{
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_snowflake_16, 0)
    }
}
