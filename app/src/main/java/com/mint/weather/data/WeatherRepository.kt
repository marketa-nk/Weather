package com.mint.weather.data

import com.mint.weather.model.DailyWeatherShort
import com.mint.weather.model.Location
import com.mint.weather.model.Time
import com.mint.weather.model.WeatherMain
import io.reactivex.Single

interface WeatherRepository {

    fun getWeatherNow(location: Location): Single<Triple<WeatherMain, List<Time>, List<DailyWeatherShort>>>

}

