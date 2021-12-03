package com.mint.weather.network.googlemaps

import com.mint.weather.data.QueryInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkServiceGoogleMaps {

    val googleMapsApi: GoogleMapsApi = getGoogleMapsApi("https://maps.googleapis.com/maps/api/geocode/")


    private fun getGoogleMapsApi(url: String): GoogleMapsApi {
        val build = OkHttpClient.Builder()
            .addInterceptor (QueryInterceptor("key", "AIzaSyBuw5zpFxq1U6EpNlwGk8gPEHEB92XGTZA"))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(GoogleMapsApi::class.java)
    }
}