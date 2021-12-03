package com.mint.weather.network.googlemaps

import com.mint.weather.network.googlemaps.CityNetworkGoogleMaps
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsApi {

    @GET("json?language=ru&result_type=locality")
    fun getCity(@Query("latlng") latlng: String): Observable<CityNetworkGoogleMaps>
}