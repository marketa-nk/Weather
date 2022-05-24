package com.mint.weather.favorites

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mint.weather.data.CityRepository
import com.mint.weather.data.WeatherRepository
import com.mint.weather.database.DataBaseRepository
import com.mint.weather.model.CityWeatherLong
import com.mint.weather.model.CityWeatherShort
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val location: Location?,
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    private val dataBaseRepository: DataBaseRepository,
) : ViewModel() {

    val citiesWeatherList: MutableLiveData<StateLong> by lazy { MutableLiveData<StateLong>() }

    private val compositeDisposable = CompositeDisposable()

    val showCurrentCityWeather: MutableLiveData<StateShort> by lazy { MutableLiveData<StateShort>() }
    val cityName: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val hideCurrentCityView: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }

    init {
        setCurrentCityWeather()
        loadFavoriteCityList()
    }

    private fun setCurrentCityWeather() {
        if (location == null) {
            hideCurrentCityView.value = Unit
        } else {
            loadCurrentLocationWeather(location)
            updateCurrentCityName(location)
        }
    }

    private fun loadCurrentLocationWeather(location: Location) {
        weatherRepository.getCurrentCityWeather(location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ weather ->
                showCurrentCityWeather.value = StateShort.Data(weather)
            }, {
                it.printStackTrace()
                showCurrentCityWeather.value = StateShort.Empty
            })
            .addDisposable()
    }

    private fun updateCurrentCityName(location: Location) {
        cityRepository.getCity(location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cityName.value = it.name
            }, {
                it.printStackTrace()
                cityName.value = ""
            })
            .addDisposable()
    }

    private fun loadFavoriteCityList() {
        dataBaseRepository.getAllCitiesFromFavorites()
            .flatMap {
                weatherRepository.getCitiesWeather(it, location)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ listWeather ->
                citiesWeatherList.value = StateLong.Data(listWeather)
            }, {
                it.printStackTrace()
                citiesWeatherList.value = StateLong.Error
            })
            .addDisposable()
    }

    fun cityClicked(city: CityWeatherLong) {

    }


    private fun Disposable.addDisposable() {
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    sealed class StateShort {
        object Empty : StateShort()
        class Data(
            val cityWeatherShort: CityWeatherShort,
        ) : StateShort()
    }

    sealed class StateLong {
        object Error : StateLong()
        class Data(
            val listCityWeatherLong: List<CityWeatherLong>,
        ) : StateLong()
    }
}