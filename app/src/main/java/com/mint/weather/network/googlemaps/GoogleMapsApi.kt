package com.mint.weather.network.googlemaps

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsApi {

    @GET("json?language=ru&result_type=locality")
    fun getCity(@Query("latlng") latlng: String): Single<CityNetworkGoogleMaps>
}