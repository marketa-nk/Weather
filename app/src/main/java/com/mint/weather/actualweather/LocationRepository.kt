package com.mint.weather.actualweather

import android.location.Location
import io.reactivex.Observable
import io.reactivex.Single

interface LocationRepository {

    fun getLocation(): Observable<Location>

    fun getLastLocation(): Single<Location>

}

