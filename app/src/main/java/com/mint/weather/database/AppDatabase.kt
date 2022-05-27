package com.mint.weather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mint.weather.model.City

@Database(entities = [City::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoriteCitiesDao
}
