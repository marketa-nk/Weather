package com.mint.weather.actualweather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mint.weather.model.Location
import io.reactivex.Observable
import io.reactivex.Single

class LocationService(context: Context) {

    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLocation(): Observable<Location> {
        return Observable.create { emitter ->
            val request = LocationRequest().also {
                it.interval = 10000
                it.fastestInterval = 5000
                it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    val location: android.location.Location? = locationResult?.lastLocation
                    if (location != null) {
                        emitter.onNext(Location(location.latitude, location.longitude))
                    }
                }
            }
            fusedLocationProvider.requestLocationUpdates(request, callback, Looper.getMainLooper())

            emitter.setCancellable {
                fusedLocationProvider.removeLocationUpdates(callback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(): Single<Location> {
        return Single.create { emitter ->
            fusedLocationProvider.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    emitter.onSuccess(Location(location.latitude, location.longitude))
                }
            }
        }
    }

    companion object {
        lateinit var instance: LocationService
    }

}

