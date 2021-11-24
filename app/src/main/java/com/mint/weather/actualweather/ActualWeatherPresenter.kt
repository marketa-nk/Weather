package com.mint.weather.actualweather

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mint.weather.data.CityRepository
import com.mint.weather.data.WeatherRepository

@InjectViewState
class ActualWeatherPresenter : MvpPresenter<WeatherView?>() {

    private val cityRepository = CityRepository()
    private val weatherRepository = WeatherRepository()

    fun sendBaseRequests(lat: Double, lon: Double) {
        weatherRepository.getWeatherNow(lat, lon) { weather, hourlyWeather, dailyWeather ->
            viewState?.showWeather(weather, hourlyWeather, dailyWeather)
        }
        cityRepository.getCity(lat, lon) {
            viewState?.setCityName(it)
        }
    }

}

