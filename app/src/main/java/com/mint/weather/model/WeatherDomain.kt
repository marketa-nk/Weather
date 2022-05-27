package com.mint.weather.model

import java.util.*

data class Weather(
    val date: Date,
    val currentWeather: CurrentWeather,
    val hourWeather: List<Time>,
    val dailyWeather: List<DailyWeather>
)

data class CurrentWeather(
    val temp: Double,
    val description: String,
    val feelsLike: Double,
    val icon: String,
    val windSpeed: Double,
    val windDeg: Long,
    val pressure: Long,
    val humidity: Long,
) {
    val pressureMM = pressure / 1.333
    val iconUrl: String = getOpenWeatherIconUrl(icon)
}

data class HourWeather(
    override val date: Date,
    val temp: Double,
    val icon: String,
    val iconUrl: String,
) : Time

data class DailyWeather(
    override val date: Date,
    val tempDay: Double,
    val tempNight: Double,
    val icon: String,
    val iconUrl: String,
    val rain: Double?,
    val snow: Double?,
    val windSpeed: Double,
    val windDirection: WindDirections,
    val windGust: Double,
) : Time

interface Time {
    val date: Date
}

fun getOpenWeatherIconUrl(icon: String): String = "https://openweathermap.org/img/wn/${icon}@2x.png"


