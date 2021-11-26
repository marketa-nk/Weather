package com.mint.weather.actualweather

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mint.weather.data.CityRepository
import com.mint.weather.data.WeatherRepository
import com.mint.weather.network.LocationService

@InjectViewState
class ActualWeatherPresenter : MvpPresenter<WeatherView?>() {

    private val cityRepository = CityRepository()
    private val weatherRepository = WeatherRepository()

    private var lat: Double = 53.9
    private var lon: Double = 27.5667

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            getCurrentLocation {
                updateCity(lat, lon)
                updateWeather(lat, lon)
            }
        } else {
            updateCity(lat, lon)
            updateWeather(lat, lon)
        }
    }

    fun swipeToRefresh() {
        updateWeather(lat, lon)
    }

    private fun getCurrentLocation(onSuccess: () -> Unit) {
        LocationService.instance.getLocation { latitude, longitude ->
            lat = latitude
            lon = longitude
            onSuccess()
        }
    }

    private fun updateCity(lat: Double, lon: Double) {
        cityRepository.getCity(lat, lon) {
            viewState?.setCityName(it)
        }
    }

    private fun updateWeather(lat: Double, lon: Double) {
        weatherRepository.getWeatherNow(lat, lon) { weather, hourlyWeather, dailyWeather ->
            viewState?.showWeather(weather, hourlyWeather, dailyWeather)
        }
    }

}

