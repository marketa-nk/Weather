package com.mint.weather.data

import com.mint.weather.model.City
import com.mint.weather.model.Location
import com.mint.weather.network.NetworkService
import io.reactivex.Single

class OpenWeatherCityRepository : CityRepository {

    private val api = NetworkService.instance.openWeatherApi

    override fun getCity(location: Location): Single<City> {
        return api.getCity(location.lat, location.lon)
            .map { response -> response.firstOrNull()?.let { City(it.localNames.ru ?: it.name) } }
    }
}