package com.mint.weather.data

import com.mint.weather.model.City
import com.mint.weather.model.Location
import com.mint.weather.network.openweather.NetworkServiceOpenWeather
import io.reactivex.Observable

class OpenWeatherCityRepository : CityRepository {

    private val networkService = NetworkServiceOpenWeather().actualOpenWeatherApi

    override fun getCity(location: Location): Observable<City> {
        return networkService.getCity(location.lat, location.lon)
            .map { response -> response.firstOrNull()?.let { City(it.localNames.ru ?: it.name) } }
    }
}