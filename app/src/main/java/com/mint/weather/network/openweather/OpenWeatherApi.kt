package com.mint.weather.network.openweather

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("data/2.5/onecall?exclude=minutely&&units=metric&lang=ru")
    fun getActualWeather(@Query("lat") lat: Double,@Query("lon") lon: Double ): Single<ActualWeather>

    @GET("geo/1.0/reverse?limit=10")
    fun getCity(@Query("lat") lat: Double, @Query("lon") lon: Double ): Single<List<CityNetwork>>
}