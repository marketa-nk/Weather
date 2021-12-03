package com.mint.weather.actualweather

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mint.weather.data.CityRepository
import com.mint.weather.data.GoogleMapsCityRepository
import com.mint.weather.data.WeatherRepository
import com.mint.weather.data.WeatherRepositoryImpl
import com.mint.weather.model.Location
import com.mint.weather.network.LocationService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@InjectViewState
class ActualWeatherPresenter : MvpPresenter<WeatherView?>() {

    private val cityRepository: CityRepository = GoogleMapsCityRepository()
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()

    private var lat: Double = 53.9
    private var lon: Double = 27.5667

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState?.requireLocationPermission()
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            getCurrentLocation {
                reload()
            }
        } else {
            reload()
        }
    }

    fun swipeToRefresh() {
        reload()
    }

    private fun reload() {
        viewState?.showProgress()
        updateWeather(lat, lon)
        updateCity(lat, lon)
    }

    private fun getCurrentLocation(onSuccess: () -> Unit) {
        LocationService.instance.getLocation { latitude, longitude ->
            lat = latitude
            lon = longitude
            onSuccess()
        }
    }

    private fun updateCity(lat: Double, lon: Double) {
        val disposable = cityRepository.getCity(Location(lat, lon))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setCityName(it.name)
            }, {
                it.printStackTrace()
            })
        compositeDisposable.add(disposable)
    }

    private fun updateWeather(lat: Double, lon: Double) {
        val disposable = weatherRepository.getWeatherNow(lat, lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (weather, hourlyWeather, dailyWeather) ->
                viewState?.showWeather(weather, hourlyWeather, dailyWeather)
                viewState?.hideProgress()
            }, {
                it.printStackTrace()
                viewState?.showEmptyWeather()
                viewState?.hideProgress()
            })
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}

