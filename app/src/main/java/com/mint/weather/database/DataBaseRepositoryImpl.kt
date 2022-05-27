package com.mint.weather.database

import com.mint.weather.model.City
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DataBaseRepositoryImpl @Inject constructor(
    private val favoriteCitiesDao: FavoriteCitiesDao,
) : DataBaseRepository {

    override fun saveCityToFavorites(city: City): Single<Long> {
        return favoriteCitiesDao.insertCity(city)
    }

    override fun deleteCityFromFavorites(city: City): Single<Int> {
        return favoriteCitiesDao.deleteCity(city)
    }

    override fun getAllCitiesFromFavorites(): Observable<List<City>> {
        return favoriteCitiesDao.getAll()
    }

    override fun isFavoriteCity(cityId: String): Single<Boolean> {
        return favoriteCitiesDao.isFavoriteCity(cityId)
            .map { it == 1 }
    }
}