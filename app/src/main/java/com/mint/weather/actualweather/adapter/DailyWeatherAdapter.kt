package com.mint.weather.actualweather.adapter

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
import com.mint.weather.model.WindDirections
import com.mint.weather.precipitationToInt
import com.mint.weather.temperatureToString
import java.text.SimpleDateFormat
import java.util.*

class DailyWeatherAdapter : ListAdapter<DailyWeatherShort, DailyWeatherAdapter.DailyWeatherViewHolder>(DailyWeatherDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val binding = ViewDailyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DailyWeatherViewHolder(private val binding: ViewDailyWeatherBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyWeatherShort: DailyWeatherShort) {

            binding.dayOfWeek.text = SimpleDateFormat("EEE", Locale.getDefault()).format(dailyWeatherShort.date)
            binding.date.text = SimpleDateFormat("d MMMM", Locale.getDefault()).format(dailyWeatherShort.date)
            binding.tempDay.text = dailyWeatherShort.tempDay.temperatureToString()
            binding.tempNight.text = dailyWeatherShort.tempNight.temperatureToString()

            setPrecipitationData(dailyWeatherShort)
            setWindData(dailyWeatherShort.windSpeed, dailyWeatherShort.windGust, dailyWeatherShort.windDirection)

            Glide
                .with(binding.icon)
                .load(dailyWeatherShort.iconUrl)
                .into(binding.icon)
        }

        private fun setPrecipitationData(dailyWeatherShort: DailyWeatherShort) {
            binding.precipitation.text = setPrecipitationVolume(dailyWeatherShort.rain, dailyWeatherShort.snow)
            binding.precipitation.setPrecipitationDrawables(dailyWeatherShort.tempDay, dailyWeatherShort.tempNight)
        }

        private fun TextView.setPrecipitationDrawables(tempDay: Double, tempNight: Double) {
            if ((tempDay + tempNight) / 2 >= 0) {
                this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_water_drop_24, 0)
            } else {
                this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_snowflake_16, 0)
            }
            this.compoundDrawablePadding = (resources.displayMetrics.density * 2f).toInt()
        }

        private fun setPrecipitationVolume(rain: Double?, snow: Double?): String {
            return when {
                rain != null && rain > 0 -> binding.root.context.getString(R.string.precipitation_volume, rain.precipitationToInt())
                snow != null && snow > 0 -> binding.root.context.getString(R.string.precipitation_volume, snow.precipitationToInt())
                else -> "— "
            }
        }

        private fun setWindData(windSpeed: Double, windGust: Double, windDirection: WindDirections) {
            binding.windSpeed.setCompoundDrawablesWithIntrinsicBounds(0, 0, windDirection.directionIcon, 0)
            binding.windSpeed.text = setWindSpeed(windSpeed, windGust)
            if (windGust > 15) {
                binding.windSpeed.setTextColor(ResourcesCompat.getColor(binding.root.resources, R.color.orange_600, binding.root.context.theme))
            } else {
                binding.windSpeed.setTextColor(binding.root.context.getColorResCompat(android.R.attr.textColorSecondary))
            }
        }

        @ColorInt
        fun Context.getColorResCompat(@AttrRes id: Int): Int {
            val resolvedAttr = TypedValue()
            theme.resolveAttribute(id, resolvedAttr, true)
            val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
            return ContextCompat.getColor(this, colorRes)
        }

        private fun setWindSpeed(windSpeed: Double, windGust: Double): String {
            return when {
                windGust > 15 -> binding.root.context.getString(R.string.wind_speed_with_wind_gust, windSpeed.toInt(), windGust.toInt())
                else -> binding.root.context.getString(R.string.wind_speed, windSpeed.toInt())
            }
        }
    }
}