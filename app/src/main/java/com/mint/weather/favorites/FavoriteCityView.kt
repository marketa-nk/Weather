package com.mint.weather.favorites

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.mint.weather.R
import com.mint.weather.databinding.FavoriteCityCompoundViewBinding
import com.mint.weather.model.City
import com.mint.weather.model.CityWeather
import com.mint.weather.model.CurrentWeather
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

    fun setCityName(city: City?) {
        if (city == null) {
            binding.cityName.text = ""
        } else {
            binding.cityName.text = city.name
        }

    }

    fun setCityWeather(weather: CurrentWeather?) {
        if (weather == null){
            showEmptyWeather()
        }else{
            binding.temp.text = resources.getString(R.string.deg_c, weather.temp.toUiStringPlusMinus())
            binding.time.text = resources.getString(R.string.current_location)
            Glide
                .with(binding.icon)
                .load(weather.iconUrl)
                .into(binding.icon)
        }
    }

    fun showEmptyWeather() {
        binding.temp.text = "-"
        binding.time.text = "--:--"
    }

    fun setCityInfo(cityWeather: CityWeather) {
        binding.cityName.text = cityWeather.city.name
        binding.time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cityWeather.weather.date)
        binding.temp.text = resources.getString(R.string.deg_c, cityWeather.weather.currentWeather.temp.toUiStringPlusMinus())
        setDistanceFromCurrentPlace(cityWeather.distanceFromCurrentPlace)
        Glide
            .with(binding.icon)
            .load(cityWeather.weather.currentWeather.iconUrl)
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