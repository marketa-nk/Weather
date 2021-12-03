package com.mint.weather.network

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.mint.weather.model.Location
import io.reactivex.Observable

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

    @SuppressLint("MissingPermission")
    fun getLocation(): Observable<Location> {
        return Observable.create { emitter ->
            fusedLocationProvider.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    emitter.onNext(Location(location.latitude, location.longitude))
                }
            }
        }
    }

    companion object {
        lateinit var instance: LocationService
    }

}

