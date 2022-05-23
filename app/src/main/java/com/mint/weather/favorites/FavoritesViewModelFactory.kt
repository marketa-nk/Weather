package com.mint.weather.favorites

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mint.weather.data.CityRepository
import com.mint.weather.data.WeatherRepository
import com.mint.weather.database.DataBaseRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class FavoritesViewModelFactory @AssistedInject constructor(
    @Assisted("location")
    private val location: Location?,
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository,
    private val dataBaseRepository: DataBaseRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(modelClass == FavoritesViewModel::class.java)
        return FavoritesViewModel(location, cityRepository, weatherRepository, dataBaseRepository) as T
    }
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("location") location: Location?): FavoritesViewModelFactory
    }
}