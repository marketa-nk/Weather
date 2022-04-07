package com.mint.weather.di

import android.content.Context
import com.mint.weather.actualweather.LocationRepository
import com.mint.weather.actualweather.LocationService
import com.mint.weather.data.CityRepository
import com.mint.weather.data.GoogleMapsCityRepository
import com.mint.weather.data.WeatherRepository
import com.mint.weather.data.WeatherRepositoryImpl
import com.mint.weather.network.NetworkService
import dagger.Module
import dagger.Provides

@Module
class Module() {

    @AppScope
    @Provides
    fun provideLocationRepository(context: Context): LocationRepository {
        return LocationService(context)
    }

    @AppScope
    @Provides
    fun provideCityRepository(networkService: NetworkService): CityRepository {
        return GoogleMapsCityRepository(networkService)
    }

    @AppScope
    @Provides
    fun provideWeatherRepository(networkService: NetworkService): WeatherRepository {
        return WeatherRepositoryImpl(networkService)
    }
}
