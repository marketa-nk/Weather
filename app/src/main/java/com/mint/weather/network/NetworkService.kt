package com.mint.weather.network

import com.mint.weather.data.QueryInterceptor
import com.mint.weather.network.googlemaps.GoogleMapsApi
import com.mint.weather.network.openweather.OpenWeatherApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class NetworkService @Inject constructor() {

    val googleMapsApi: GoogleMapsApi = getApi(GoogleMapsApi::class.java, "https://maps.googleapis.com/maps/api/geocode/", QueryInterceptor("key", "AIzaSyBuw5zpFxq1U6EpNlwGk8gPEHEB92XGTZA"))
    val openWeatherApi: OpenWeatherApi = getApi(OpenWeatherApi::class.java, "https://api.openweathermap.org/", QueryInterceptor("appid", "b7044fa387aaefecbb6a8888f3624867"))

    private fun <T> getApi(clazz: Class<T>, url: String, vararg interceptors: Interceptor): T {

        val httpClient = OkHttpClient.Builder()
            .also { builder ->
                interceptors.forEach {
                    builder.addInterceptor(it)
                }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(clazz)
    }
}