package com.mint.weather.actualweather

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mint.weather.model.DailyWeatherShort
import com.mint.weather.model.Time
import com.mint.weather.model.WeatherMain

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface WeatherView : MvpView {
    fun setCityName(city: String)
    fun showWeather(weather: WeatherMain, hourlyWeather: List<Time>, dailyWeather: List<DailyWeatherShort>)
    fun requireLocationPermission()
}
