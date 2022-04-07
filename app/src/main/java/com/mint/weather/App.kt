package com.mint.weather

import android.app.Application
import com.mint.weather.di.AppComponent
import com.mint.weather.di.ContextModule
import com.mint.weather.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()

        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }

}