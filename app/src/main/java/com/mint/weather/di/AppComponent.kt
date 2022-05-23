package com.mint.weather.di

import com.mint.weather.actualweather.ActualWeatherFragment
import com.mint.weather.favorites.FavoritesFragment
import com.mint.weather.network.NetworkService
import dagger.Component
import javax.inject.Scope

@AppScope
@Component(modules = [Module::class, ContextModule::class, DatabaseModule::class])
interface AppComponent {

    fun injectActualWeatherFragment(actualWeatherFragment: ActualWeatherFragment)
    fun injectFavoritesFragment(favoritesFragment: FavoritesFragment)
    fun getNetworkService(): NetworkService
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope