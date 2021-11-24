package com.mint.weather.model

import java.util.*


data class HourWeather(
    override val date: Date,
    val temp: Double,
    val icon: String
): Time

data class DailyWeatherShort(
    override val date: Date,
    val tempDay: Double,
    val tempNight: Double,
    val icon: String,
    val rain: Double?,
    val windSpeed: Double,
    val windDirection: WindDirections,
    val windGust: Double,
): Time

data class DailyWeatherLong(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: Double,
    val temp: Temperature,
    val feelsLike: WeatherFeelsLike,
    val pressure: Long,
    val humidity: Long,
    val dewPoint: Double,
    val windSpeed: Double,
    val windDeg: Long,
    val windGust: Double,
    val weather: List<WeatherDescription>,
    val clouds: Long,
    val pop: Double,
    val uvi: Double,
    val rain: Double? = null,
    val snow: Double? = null
)
data class Temperature(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)
data class WeatherDescription(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)
data class WeatherFeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)
data class WeatherMain(
    val temp: Double,
    val description: String,
    val feelsLike: Double,
    val icon: String,
    val windSpeed: Double,
    val windDeg: Long,
    val pressure: Long,
    val humidity: Long,
)
interface Time {
    val date: Date
}



