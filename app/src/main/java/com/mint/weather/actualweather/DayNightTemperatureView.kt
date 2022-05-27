package com.mint.weather.actualweather

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.mint.weather.R
import com.mint.weather.databinding.DayNightTempCompoundViewBinding
import com.mint.weather.temperatureToString

class DayNightTemperatureView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayoutCompat(context, attributeSet, defStyleAttr) {

    private val binding = DayNightTempCompoundViewBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, this)

    fun setNightTemperature(temperature: Double) {
        binding.temp.text = temperature.temperatureToString()
//        binding.temp.textSize = binding.temp.textSize * 0.3f
        binding.temp.setTypeface(null, Typeface.NORMAL)
        binding.img.setImageResource(R.drawable.ic_moon_16)
    }

    fun setDayTemperature(temperature: Double) {
        binding.temp.text = temperature.temperatureToString()
        binding.temp.setTypeface(null, Typeface.BOLD)
        binding.img.setImageResource(R.drawable.ic_sunny_16)

    }
}