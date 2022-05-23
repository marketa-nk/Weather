package com.mint.weather.model

import java.util.*

data class CityWeatherShort(
    val temperature: Double,
    val icon: String,
)

data class CityWeatherLong(
    val cityId: String,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val date: Date,
    val timezoneOffset: Long,
    val temperature: Double,
    val icon: String,
    val distanceFromCurrentPlace: Double
)