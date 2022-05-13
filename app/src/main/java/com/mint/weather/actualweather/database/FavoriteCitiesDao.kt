package com.mint.weather.actualweather.database

import androidx.room.*
import com.mint.weather.model.FavoriteCity
import io.reactivex.Single

@Dao
interface FavoriteCitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(favoriteCity: FavoriteCity): Single<Long>

    @Delete
    fun deleteCity(favoriteCity: FavoriteCity): Single<Int>

    @Query("SELECT * FROM FavoriteCity")
    fun getAll(): Single<List<FavoriteCity>>

    @Query("SELECT COUNT(*) FROM FavoriteCity WHERE cityId = :id")
    fun isFavoriteCity(id: String): Single<Int>
}
