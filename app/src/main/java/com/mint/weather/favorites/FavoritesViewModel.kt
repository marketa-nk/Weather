package com.mint.weather.favorites

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mint.weather.SingleLiveEvent
import com.mint.weather.data.WeatherRepository
import com.mint.weather.database.DataBaseRepository
import com.mint.weather.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val cityWeather: CurrentCityWeather?,
    private val weatherRepository: WeatherRepository,
    private val dataBaseRepository: DataBaseRepository,
) : ViewModel() {

    val showErrorFavoritesWeather: SingleLiveEvent<Unit> by lazy { SingleLiveEvent<Unit>() }

    private val compositeDisposable = CompositeDisposable()

    val showCitiesWeatherList: MutableLiveData<List<FavoritesItem>> by lazy { MutableLiveData<List<FavoritesItem>>() }
    val city: MutableLiveData<City> by lazy { MutableLiveData<City>() }

    init {
        showCitiesWeatherList.value = getFavoritesItemsList(cityWeather)
        loadFavoriteCityList(cityWeather?.city)
    }

    private fun loadFavoriteCityList(currentCity: City?) {
        var loc: Location? = null
        if (currentCity != null) {
            loc = Location("loc").also {
                it.latitude = currentCity.latitude
                it.longitude = currentCity.longitude
            }
        }
        dataBaseRepository.getAllCitiesFromFavorites()
            .flatMap {
                weatherRepository.getCitiesWeather(it, loc)
            }
            .map { listWeather ->
                getFavoritesItemsList(cityWeather, listWeather)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                showCitiesWeatherList.value = it
            }, {
                showErrorFavoritesWeather.value = Unit
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun getFavoritesItemsList(cityWeather: CurrentCityWeather?, listWeather: List<CityWeather> = emptyList()): List<FavoritesItem> {
        val list = mutableListOf<FavoritesItem>()
        if (cityWeather?.currentWeather != null) {
            list.addAll(listOf(cityWeather, FavoritesText("Избранное")))
        }
        return list + listWeather
    }

    fun cityClicked(city: CityWeather) {

    }


    private fun Disposable.addDisposable() {
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}