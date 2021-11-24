package com.mint.weather.network

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {

    val actualWeatherApi: WeatherApi = getActualWeatherApi("https://api.openweathermap.org/")

    private fun getActualWeatherApi(url: String): WeatherApi {
        val build = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request()

                val newUrl: HttpUrl = request.url()
                    .newBuilder()
                    .addQueryParameter("appid", "b7044fa387aaefecbb6a8888f3624867")
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

        return retrofit.create(WeatherApi::class.java)
    }
}