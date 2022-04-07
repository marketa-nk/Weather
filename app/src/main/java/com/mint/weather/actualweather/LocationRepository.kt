package com.mint.weather.actualweather

import com.mint.weather.model.Location
import io.reactivex.Observable
import io.reactivex.Single

interface LocationRepository {

    fun getLocation(): Observable<Location>

    fun getLastLocation(): Single<Location>

}

