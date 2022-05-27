package com.mint.weather.data

import android.location.Location
import com.mint.weather.model.*
import io.reactivex.Observable
import io.reactivex.Single

interface WeatherRepository {

    fun getWeather(location: Location): Single<Weather>
    fun getCitiesWeather(cityList: List<City>, currentLocation: Location?): Observable <List<CityWeather>>

}

