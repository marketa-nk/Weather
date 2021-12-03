package com.mint.weather.network.openweather

import com.mint.weather.data.QueryInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkServiceOpenWeather {

    val actualOpenWeatherApi: OpenWeatherApi = getActualWeatherApi("https://api.openweathermap.org/")

    private fun getActualWeatherApi(url: String): OpenWeatherApi {
        val build = OkHttpClient.Builder()
            .addInterceptor(QueryInterceptor("appid", "b7044fa387aaefecbb6a8888f3624867"))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(OpenWeatherApi::class.java)
    }
}