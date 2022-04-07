package com.mint.weather.data

import com.mint.weather.model.City
import com.mint.weather.model.Location
import com.mint.weather.network.NetworkService
import io.reactivex.Single
import javax.inject.Inject

class OpenWeatherCityRepository @Inject constructor(private val networkService: NetworkService) : CityRepository {

    private val api = networkService.openWeatherApi

    override fun getCity(location: Location): Single<City> {
        return api.getCity(location.lat, location.lon)
            .map { response -> response.firstOrNull()?.let { City(it.localNames.ru ?: it.name) } }
    }
}