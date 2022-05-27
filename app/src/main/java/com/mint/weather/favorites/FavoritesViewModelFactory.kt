package com.mint.weather.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mint.weather.data.WeatherRepository
import com.mint.weather.database.DataBaseRepository
import com.mint.weather.model.CurrentCityWeather
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class FavoritesViewModelFactory @AssistedInject constructor(
    @Assisted("weather")
    private val weatherInCurrentPlace: CurrentCityWeather?,
    private val weatherRepository: WeatherRepository,
    private val dataBaseRepository: DataBaseRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(modelClass == FavoritesViewModel::class.java)
        return FavoritesViewModel(weatherInCurrentPlace, weatherRepository, dataBaseRepository) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("weather") weatherInCurrentPlace: CurrentCityWeather?): FavoritesViewModelFactory
    }
}