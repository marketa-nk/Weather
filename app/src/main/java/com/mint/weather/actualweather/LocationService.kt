package com.mint.weather.actualweather

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class LocationService @Inject constructor(context: Context) : LocationRepository {

    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLocation(): Observable<Location> {
        return Observable.create { emitter ->
            val request = LocationRequest().also {
                it.interval = 10000
                it.fastestInterval = 5000
                it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    val location: Location? = locationResult?.lastLocation
                    if (location != null) {
                        emitter.onNext(Location("loc").also {
                            it.latitude = location.latitude
                            it.longitude = location.longitude
                        })
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
    override fun getLastLocation(): Single<Location> {
        return Single.create { emitter ->
            fusedLocationProvider.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    emitter.onSuccess(Location("loc").also {
                        it.latitude = location.latitude
                        it.longitude = location.longitude
                    })
                }
            }
        }
    }
}

