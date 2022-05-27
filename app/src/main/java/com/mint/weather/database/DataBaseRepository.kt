package com.mint.weather.database

import com.mint.weather.model.City
import io.reactivex.Observable
import io.reactivex.Single

interface DataBaseRepository {
    fun saveCityToFavorites(city: City): Single<Long>
    fun deleteCityFromFavorites(city: City): Single<Int>
    fun getAllCitiesFromFavorites(): Observable<List<City>>
    fun isFavoriteCity(cityId: String): Single<Boolean>
}