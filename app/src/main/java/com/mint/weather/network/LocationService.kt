package com.mint.weather.network

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

class LocationService(context: Context) {

    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLocation(onSuccess: (Double, Double) -> Unit) {
        fusedLocationProvider.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            }
        }
    }

    companion object {
        lateinit var instance: LocationService
    }

}