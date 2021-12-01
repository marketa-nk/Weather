package com.mint.weather.data

import com.mint.weather.model.DailyWeatherShort
import com.mint.weather.model.Time
import com.mint.weather.model.WeatherMain

interface WeatherRepository {

    fun getWeatherNow(lat: Double, lon: Double, onSuccess: (WeatherMain, List<Time>, List<DailyWeatherShort>) -> Unit, onFailure: (Throwable) -> Unit)

}

