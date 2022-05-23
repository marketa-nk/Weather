package com.mint.weather.database

import com.mint.weather.model.FavoriteCity
import io.reactivex.Observable
import io.reactivex.Single

interface DataBaseRepository {
    fun saveCityToFavorites(favoriteCity: FavoriteCity): Single<Long>
    fun deleteCityFromFavorites(favoriteCity: FavoriteCity): Single<Int>
    fun getAllCitiesFromFavorites(): Observable<List<FavoriteCity>>
    fun isFavoriteCity(cityId: String): Single<Boolean>
}