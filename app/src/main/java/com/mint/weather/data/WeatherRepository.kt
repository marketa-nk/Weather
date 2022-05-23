package com.mint.weather.data

import android.location.Location
import com.mint.weather.model.*
import io.reactivex.Observable
import io.reactivex.Single

interface WeatherRepository {

    fun getWeatherNow(location: Location): Single<Triple<WeatherMain, List<Time>, List<DailyWeatherShort>>>
    fun getCurrentCityWeather(location: Location): Single<CityWeatherShort>
    fun getCitiesWeather(cityList: List<FavoriteCity>, currentLocation: Location?): Observable <List<CityWeatherLong>>

}

