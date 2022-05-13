package com.mint.weather.actualweather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.mint.weather.SingleLiveEvent
import com.mint.weather.actualweather.database.DataBaseRepository
import com.mint.weather.data.CityRepository
import com.mint.weather.data.WeatherRepository
import com.mint.weather.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ActualWeatherViewModel(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val dataBaseRepository: DataBaseRepository,
) : ViewModel() {

    private var location: Location? = null

    private var currentCity: CurrentCity? = null

    val showProgress: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val checkLocationPermissionEvent: SingleLiveEvent<Unit> by lazy { SingleLiveEvent<Unit>() }
    val cityName: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val showFindCitiesScreen: SingleLiveEvent<Unit> by lazy { SingleLiveEvent<Unit>() }

    val showWeather: MutableLiveData<State> by lazy { MutableLiveData<State>() }

    val requireLocationPermissionEvent: SingleLiveEvent<Unit> by lazy { SingleLiveEvent() }

    val fillTheFavoriteStar: MutableLiveData<Boolean?> by lazy { MutableLiveData<Boolean?>() }

    private val compositeDisposable = CompositeDisposable()

    init {
        requireLocationPermissionEvent.value = Unit
    }

    fun permissionGranted(granted: Boolean) {
        if (granted) {
            getCurrentLocation()
        } else {
            showWeather.value = State.Empty
        }
    }

    fun viewSwipedToRefresh() {
        reload()
    }

    private fun reload() {
        val location = location
        fillTheFavoriteStar.value = null
        currentCity = null
        if (location != null) {
            showProgress.value = true
            updateWeather(location)
            updateCity(location)
        } else {
            showProgress.value = false
            showWeather.value = State.Empty
            cityName.value = ""
        }
    }

    fun searchPressed() {
        showFindCitiesScreen.value = Unit
    }

    fun cityIsSelected(cityId: String, name: String?, latLng: LatLng) {
        showProgress.value = true
        val selectedCity = CurrentCity(cityId, name ?: "", latLng.latitude, latLng.longitude)
        cityName.value = selectedCity.name
        fillOrNotFavoriteStar(selectedCity.cityId)
        updateWeather(Location(selectedCity.lat, selectedCity.lng))
        currentCity = selectedCity
    }


    private fun fillOrNotFavoriteStar(cityId: String) {
        dataBaseRepository.isFavoriteCity(cityId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                fillTheFavoriteStar.value = it
            }, {
                it.printStackTrace()
            })
            .addDisposable()
    }

    private fun getCurrentLocation() {
        locationRepository.getLastLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                location = it
                reload()
            }, {
                location = null
                reload()
                it.printStackTrace()
            })
            .addDisposable()

    }

    private fun updateCity(location: Location) {
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

    private fun updateWeather(location: Location) {
        weatherRepository.getWeatherNow(location)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (weather, hourlyWeather, dailyWeather) ->
                showWeather.value = State.Data(weather, hourlyWeather, dailyWeather)
                showProgress.value = false
            }, {
                it.printStackTrace()
                showWeather.value = State.Empty
                showProgress.value = false
            })
            .addDisposable()
    }

    fun favoriteBtnPressed() {
        val city = currentCity
        if (city != null) {
            val favoriteCity = FavoriteCity(city.cityId, city.name, city.lat, city.lng)
            dataBaseRepository.isFavoriteCity(city.cityId)
                .flatMap { favorite ->
                    if (favorite) {
                        dataBaseRepository.deleteCityFromFavorites(favoriteCity)
                    } else {
                        dataBaseRepository.saveCityToFavorites(favoriteCity)
                    }
                }
                .ignoreElement()
                .andThen(dataBaseRepository.isFavoriteCity(city.cityId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    fillTheFavoriteStar.value = it
                }, {
                    it.printStackTrace()
                })
                .addDisposable()
        }
    }

    private fun Disposable.addDisposable() {
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    class ActualWeatherViewModelFactory @Inject constructor(
        private val cityRepository: CityRepository,
        private val weatherRepository: WeatherRepository,
        private val locationRepository: LocationRepository,
        private val dataBaseRepository: DataBaseRepository,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ActualWeatherViewModel::class.java)
            return ActualWeatherViewModel(cityRepository, weatherRepository, locationRepository, dataBaseRepository) as T
        }
    }

    sealed class State {
        object Empty : State()
        class Data(
            val currentWeather: WeatherMain,
            val hourlyWeather: List<Time>,
            val dailyWeather: List<DailyWeatherShort>,
        ) : State()
    }
}