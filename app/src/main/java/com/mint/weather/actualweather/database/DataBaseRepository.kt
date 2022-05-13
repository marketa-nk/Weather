package com.mint.weather.actualweather.database

import com.mint.weather.model.FavoriteCity
import io.reactivex.Single

interface DataBaseRepository {
    fun saveCityToFavorites(favoriteCity: FavoriteCity): Single<Long>
    fun deleteCityFromFavorites(favoriteCity: FavoriteCity): Single<Int>
    fun getAllCitiesFromFavorites(): Single<List<FavoriteCity>>
    fun isFavoriteCity(cityId: String): Single<Boolean>
}