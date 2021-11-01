package com.mint.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("geo/1.0/direct")
    fun getCities(@Query("q") city: String): Call<List<City>>

    @GET("data/2.5/weather?units=metric&lang=ru")
    fun getWeatherNow(@Query("lat") lat: Double,@Query("lon") lon: Double ): Call<WeatherNow>


}