package com.mint.weather

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.mint.weather.di.AppComponent
import com.mint.weather.di.ContextModule
import com.mint.weather.di.DaggerAppComponent
import com.mint.weather.di.DatabaseModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .databaseModule(DatabaseModule(this))
            .build()

        instance = this

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key_google))
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }

}