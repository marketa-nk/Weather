package com.mint.weather

import android.app.Application
import com.mint.weather.network.LocationService

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LocationService.instance = LocationService(applicationContext)
    }

}