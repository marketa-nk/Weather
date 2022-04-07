package com.mint.weather.di

import com.mint.weather.actualweather.ActualWeatherFragment
import dagger.Component
import javax.inject.Scope

@AppScope
@Component(modules = [Module::class, ContextModule::class])
interface AppComponent {

    fun injectActualWeatherFragment(actualWeatherFragment: ActualWeatherFragment)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope