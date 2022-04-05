package com.mint.weather.actualweather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mint.weather.R
import com.mint.weather.databinding.ViewHourWeatherBinding
import com.mint.weather.model.HourWeather
import com.mint.weather.model.Sunrise
import com.mint.weather.model.Sunset
import java.text.SimpleDateFormat
import java.util.*

class HourlyWeatherAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(HourlyWeatherDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ViewHourWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return when (viewType) {
            TYPE_HOUR_WEATHER -> HourlyWeatherViewHolder(binding)
            TYPE_SUNRISE -> SunriseViewHolder(binding)
            TYPE_SUNSET -> SunsetViewHolder(binding)
            else -> throw java.lang.Exception()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_HOUR_WEATHER -> (holder as HourlyWeatherViewHolder).bind(getItem(position) as HourWeather)
            TYPE_SUNRISE -> (holder as SunriseViewHolder).bind(getItem(position) as Sunrise)
            TYPE_SUNSET -> (holder as SunsetViewHolder).bind(getItem(position) as Sunset)
            else -> throw IllegalStateException("Unexpected value: " + holder.itemViewType)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HourWeather -> TYPE_HOUR_WEATHER
            is Sunrise -> TYPE_SUNRISE
            is Sunset -> TYPE_SUNSET
            else -> throw IllegalStateException("Unexpected value")
        }
    }

    inner class SunriseViewHolder(private val binding: ViewHourWeatherBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sunrise: Sunrise) {
            binding.time.text = SimpleDateFormat("HH:mm").format(sunrise.date)
            binding.temp.text = "восход"
            binding.icon.setPadding(28,28,28,28)
            Glide
                .with(binding.icon)
                .load(R.drawable.ic_sunrise_24)
                .fitCenter()
                .into(binding.icon)
        }
    }

    inner class SunsetViewHolder(private val binding: ViewHourWeatherBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sunset: Sunset) {
            binding.time.text = SimpleDateFormat("HH:mm").format(sunset.date)
            binding.temp.text = "закат"
            binding.icon.setPadding(28,28,28,28)
            Glide
                .with(binding.icon)
                .load(R.drawable.ic_sunset_24)
                .fitCenter()
                .into(binding.icon)
        }
    }

    inner class HourlyWeatherViewHolder(private val binding: ViewHourWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hourWeather: HourWeather) {

            binding.time.text = convertDateToString(hourWeather.date)
            binding.temp.text = temperatureToString(hourWeather.temp)

            Glide
                .with(binding.icon)
                .load("https://openweathermap.org/img/wn/${hourWeather.icon}@2x.png")
                .into(binding.icon)
        }
    }

    fun temperatureToString(temp: Double): String {
        return when {
            temp >= 1 -> "+${temp.toInt()}°C"
            else -> "${temp.toInt()}°C"
        }
    }

    fun convertDateToString(s: Date): String {
        val time = SimpleDateFormat("HH:mm").format(s)
        return when {
            time.equals("00:00") ->
                """$time
                |${SimpleDateFormat("d MMM").format(s)}""".trimMargin()
            else -> time
        }
    }

    companion object {
        private const val TYPE_HOUR_WEATHER = 1
        private const val TYPE_SUNRISE = 2
        private const val TYPE_SUNSET = 3
    }
}