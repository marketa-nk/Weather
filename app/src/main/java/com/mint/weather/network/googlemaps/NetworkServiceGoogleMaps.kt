package com.mint.weather.network.googlemaps

import com.mint.weather.network.openweather.OpenWeatherApi
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkServiceGoogleMaps {

    val googleMapsApi: GoogleMapsApi = getGoogleMapsApi("https://maps.googleapis.com/maps/api/geocode/")

    private fun getGoogleMapsApi(url: String): GoogleMapsApi {
        val build = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request()

                val newUrl: HttpUrl = request.url()
                    .newBuilder()
                    .addQueryParameter("key", "AIzaSyBuw5zpFxq1U6EpNlwGk8gPEHEB92XGTZA")
                    .build()

                val newRequest = request.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GoogleMapsApi::class.java)
    }
}