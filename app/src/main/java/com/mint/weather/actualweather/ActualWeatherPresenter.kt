package com.mint.weather.actualweather

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mint.weather.data.*
import com.mint.weather.network.LocationService

@InjectViewState
class ActualWeatherPresenter : MvpPresenter<WeatherView?>() {

    private val cityRepository: CityRepository = CityRepositoryGoogleMaps()
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()

    private var lat: Double = 53.9
    private var lon: Double = 27.5667

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            getCurrentLocation {
//                updateCity(lat, lon)
//                updateWeather(lat, lon)
                swipeToRefresh()
            }
        } else {
//            updateCity(lat, lon)
//            updateWeather(lat, lon)
            swipeToRefresh()
        }
    }

    fun swipeToRefresh() {
        viewState?.showProgress()
        updateWeather(lat, lon)
        updateCity(lat, lon)
        viewState?.hideProgress()
    }

    private fun getCurrentLocation(onSuccess: () -> Unit) {
        LocationService.instance.getLocation { latitude, longitude ->
            lat = latitude
            lon = longitude
            onSuccess()
        }
    }

    private fun updateCity(lat: Double, lon: Double) {
        cityRepository.getCity(
            lat, lon,
            onSuccess = {
                viewState?.setCityName(it)
            },
            onFailure = {
                viewState?.setCityName("")

            }
        )
    }

    private fun updateWeather(lat: Double, lon: Double) {
        weatherRepository.getWeatherNow(
            lat, lon,
            onSuccess = { weather, hourlyWeather, dailyWeather ->
                viewState?.showWeather(weather, hourlyWeather, dailyWeather)
//                viewState?.showEmptyWeather()
            },
            onFailure = {
                viewState?.showEmptyWeather()
            }
        )
    }

}

