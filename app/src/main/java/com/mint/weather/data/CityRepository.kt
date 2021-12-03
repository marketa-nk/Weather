package com.mint.weather.data

import com.mint.weather.model.City
import com.mint.weather.model.Location

import io.reactivex.Observable

interface CityRepository {
    fun getCity(location: Location): Observable<City>
}