package com.mint.weather.database

import androidx.room.*
import com.mint.weather.model.City
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface FavoriteCitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city: City): Single<Long>

    @Delete
    fun deleteCity(city: City): Single<Int>

    @Query("SELECT * FROM City")
    fun getAll(): Observable<List<City>>

    @Query("SELECT COUNT(*) FROM City WHERE id = :id")
    fun isFavoriteCity(id: String): Single<Int>
}
