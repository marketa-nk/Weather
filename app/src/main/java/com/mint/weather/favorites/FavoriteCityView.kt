package com.mint.weather.favorites

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.mint.weather.R
import com.mint.weather.databinding.FavoriteCityCompoundViewBinding
import com.mint.weather.model.CityWeatherLong
import com.mint.weather.model.CityWeatherShort
import com.mint.weather.toUiString
import com.mint.weather.toUiStringPlusMinus
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class FavoriteCityView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val binding = FavoriteCityCompoundViewBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, this)

    fun setCityName(name: String) {
        binding.cityName.text = name
    }

    fun setCityWeather(cityWeatherShort: CityWeatherShort) {
        binding.temp.text = resources.getString(R.string.deg_c, cityWeatherShort.temperature.toUiString())
        binding.time.text = resources.getString(R.string.current_location)
        Glide
            .with(binding.icon)
            .load(cityWeatherShort.icon)
            .into(binding.icon)
    }

    fun showEmptyWeather() {
        binding.temp.text = "-"
        binding.time.text = "--:--"
    }

    fun setCityInfo(cityWeatherLong: CityWeatherLong) {
        binding.cityName.text = cityWeatherLong.cityName
        binding.time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cityWeatherLong.date)
        binding.temp.text = resources.getString(R.string.deg_c, cityWeatherLong.temperature.toUiStringPlusMinus())
        setDistanceFromCurrentPlace(cityWeatherLong.distanceFromCurrentPlace)
        Glide
            .with(binding.icon)
            .load(cityWeatherLong.icon)
            .into(binding.icon)
    }

    private fun setDistanceFromCurrentPlace(distance: Double?) {
        if (distance != null) {
            binding.distance.text = resources.getString(R.string.distanceInKm, DecimalFormat("#").format(distance))
        } else {
            binding.distance.text = ""
        }
    }
}