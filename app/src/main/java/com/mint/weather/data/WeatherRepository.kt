package com.mint.weather.data

import com.mint.weather.model.DailyWeatherShort
import com.mint.weather.model.Time
import com.mint.weather.model.WeatherMain
import io.reactivex.Observable

interface WeatherRepository {

    fun getWeatherNow(lat: Double, lon: Double): Observable<Triple<WeatherMain, List<Time>, List<DailyWeatherShort>>>

}

