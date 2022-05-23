package com.mint.weather.database

import com.mint.weather.model.FavoriteCity
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DataBaseRepositoryImpl @Inject constructor(
    private val favoriteCitiesDao: FavoriteCitiesDao,
) : DataBaseRepository {

    override fun saveCityToFavorites(favoriteCity: FavoriteCity): Single<Long> {
        return favoriteCitiesDao.insertCity(favoriteCity)
    }

    override fun deleteCityFromFavorites(favoriteCity: FavoriteCity): Single<Int> {
        return favoriteCitiesDao.deleteCity(favoriteCity)
    }

    override fun getAllCitiesFromFavorites(): Observable<List<FavoriteCity>> {
        return favoriteCitiesDao.getAll()
    }

    override fun isFavoriteCity(cityId: String): Single<Boolean> {
        return favoriteCitiesDao.isFavoriteCity(cityId)
            .map { it == 1 }
    }
}