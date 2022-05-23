package com.mint.weather.data

import android.location.Location
import com.mint.weather.model.City

import io.reactivex.Single

interface CityRepository {
    fun getCity(location: Location): Single<City>
}