package com.mint.weather.actualweather

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mint.weather.data.CityRepository
import com.mint.weather.data.GoogleMapsCityRepository
import com.mint.weather.data.WeatherRepository
import com.mint.weather.data.WeatherRepositoryImpl
import com.mint.weather.model.Location
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@InjectViewState
class ActualWeatherPresenter : MvpPresenter<WeatherView?>() {

    private val cityRepository: CityRepository = GoogleMapsCityRepository()
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl()

    private var location: Location = Location(53.9, 27.5667)
//    private var lat: Double = 53.9
//    private var lon: Double = 27.5667

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
        updateWeather(location)
        updateCity(location)
    }


    private fun getCurrentLocation(onSuccess: () -> Unit) {
        LocationService.instance.getLastLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                location = it
                onSuccess()
            }, {
                it.printStackTrace()
            })
            .addDisposable()

    }

    private fun updateCity(location: Location) {
        cityRepository.getCity(location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setCityName(it.name)
            }, {
                it.printStackTrace()
                viewState?.setCityName("")
            })
            .addDisposable()
    }

    private fun updateWeather(location: Location) {
        weatherRepository.getWeatherNow(location)
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
            .addDisposable()
    }

    private fun Disposable.addDisposable() {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}

