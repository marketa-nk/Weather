package com.mint.weather

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mint.weather.databinding.ViewDailyWeatherBinding
import java.text.SimpleDateFormat
import java.util.*

class DailyWeatherAdapter(
    private var items: List<DailyWeatherShort>,
) : RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val binding = ViewDailyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class DailyWeatherViewHolder(private val binding: ViewDailyWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dailyWeatherShort: DailyWeatherShort) {

            binding.dayOfWeek.text = convertLongToDayOfWeek(dailyWeatherShort.dateTime)
            binding.date.text = convertLongToDate(dailyWeatherShort.dateTime)
            binding.tempDay.text = temperatureToString(dailyWeatherShort.tempDay)
            binding.tempNight.text = temperatureToString(dailyWeatherShort.tempNight)
            binding.rain.text = setPrecipitationVolume(dailyWeatherShort.rain)
            binding.rain.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_water_drop_24, 0)

            binding.windSpeed.setCompoundDrawablesWithIntrinsicBounds(0, 0, dailyWeatherShort.windDirection.directionIcon, 0)
            binding.windSpeed.text = setWindSpeed(dailyWeatherShort.windSpeed, dailyWeatherShort.windGust, dailyWeatherShort.windDirection)
            if (dailyWeatherShort.windGust > 15){
                binding.windSpeed.setTextColor(ResourcesCompat.getColor(binding.root.resources, R.color.red_600, binding.root.context.theme))
            } else {
                binding.windSpeed.setTextColor(binding.root.context.getColorResCompat(android.R.attr.textColorSecondary))
            }

            Glide
                .with(binding.root)
                .load("https://openweathermap.org/img/wn/${dailyWeatherShort.icon}@2x.png")
                .into(binding.icon)
        }

        private fun setWindSpeed(windSpeed: Double, windGust: Double, windDirection: WindDirections): String {
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

    fun setPrecipitationVolume(rain: Double?): String {
        return when {
            rain != null && rain > 0 -> "${kotlin.math.ceil(rain).toInt()}мм "
            else -> "— "
        }
    }

    fun temperatureToString(temp: Double): String {
        return when {
            temp >= 1 -> "+${temp.toInt()}°C"
            else -> "${temp.toInt()}°C"
        }
    }

    fun convertLongToDayOfWeek(s: Long): String? {
        return try {
            SimpleDateFormat("EEE").format(Date(s * 1000))
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun convertLongToDate(s: Long): String? {
        return try {
            SimpleDateFormat("d MMMM").format(Date(s * 1000))
        } catch (e: Exception) {
            e.toString()
        }
    }
}