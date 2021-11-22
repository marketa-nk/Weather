package com.mint.weather.actualweather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mint.weather.R
import com.mint.weather.databinding.ViewHourWeatherBinding
import com.mint.weather.model.HourWeather
import com.mint.weather.model.Sunrise
import com.mint.weather.model.Sunset
import java.text.SimpleDateFormat
import java.util.*

class HourlyWeatherAdapter(
    private var items: List<Any>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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
            TYPE_HOUR_WEATHER -> (holder as HourlyWeatherViewHolder).bind(items[position] as HourWeather)
            TYPE_SUNRISE -> (holder as SunriseViewHolder).bind(items[position] as Sunrise)
            TYPE_SUNSET -> (holder as SunsetViewHolder).bind(items[position] as Sunset)
            else -> throw IllegalStateException("Unexpected value: " + holder.itemViewType)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HourWeather -> TYPE_HOUR_WEATHER
            is Sunrise -> TYPE_SUNRISE
            is Sunset -> TYPE_SUNSET
            else -> throw IllegalStateException("Unexpected value")
        }
    }
    fun setItems(items: List<Any>){
        this.items = items
        notifyItemRangeChanged(0,items.size)
    }

    inner class SunriseViewHolder(private val binding: ViewHourWeatherBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sunrise: Sunrise) {
            binding.time.text = convertLongToTime(sunrise.dateTime)
            binding.temp.text = "восход"
            Glide
                .with(itemView)
                .load(R.drawable.sunrise)
                .fitCenter()
                .into(binding.icon)
        }
    }

    inner class SunsetViewHolder(private val binding: ViewHourWeatherBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sunset: Sunset) {
            binding.time.text = convertLongToTime(sunset.dateTime)
            binding.temp.text = "закат"
            binding.icon.setPadding(9,9,9,9)
            Glide
                .with(itemView)
                .load(R.drawable.sunset)
                .fitCenter()
                .into(binding.icon)
        }
    }

    inner class HourlyWeatherViewHolder(private val binding: ViewHourWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hourWeather: HourWeather) {

            binding.time.text = convertLongToTime(hourWeather.dateTime)
            binding.temp.text = temperatureToString(hourWeather.temp)

            Glide
                .with(binding.root)
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

    fun convertLongToTime(s: Long): String {
        val time = SimpleDateFormat("HH:mm").format(Date(s * 1000))
        return when {
            time.equals("00:00") ->
                """$time
                |${convertLongToDate(s)}""".trimMargin()
            else -> time
        }
    }

    fun convertLongToDate(s: Long): String? {
        return try {
            SimpleDateFormat("d MMM").format(Date(s * 1000))
        } catch (e: Exception) {
            e.toString()
        }
    }

    companion object {
        private const val TYPE_HOUR_WEATHER = 1
        private const val TYPE_SUNRISE = 2
        private const val TYPE_SUNSET = 3
    }
}