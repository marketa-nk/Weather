package com.mint.weather.di

import android.content.Context
import androidx.room.Room
import com.mint.weather.database.AppDatabase
import com.mint.weather.database.DataBaseRepository
import com.mint.weather.database.DataBaseRepositoryImpl
import com.mint.weather.database.FavoriteCitiesDao
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule(private val context: Context) {

    @AppScope
    @Provides
    fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .build()
    }

    @Provides
    fun provideFavoritesDao(database: AppDatabase): FavoriteCitiesDao {
        return database.favoritesDao()
    }

    @AppScope
    @Provides
    fun provideDatabaseRepository(favoriteCitiesDao: FavoriteCitiesDao): DataBaseRepository {
        return DataBaseRepositoryImpl(favoriteCitiesDao)
    }
}