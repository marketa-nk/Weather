package com.mint.weather.network

import com.google.gson.annotations.SerializedName


data class ActualWeather(
    val lat: Double,
    val lon: Double,
    val timezone: String,

    @SerializedName("timezone_offset")
    val timezoneOffset: Long,

    val current: Current,
    val hourly: List<Current>,
    val daily: List<Daily>
)

data class Current(
    val dt: Long,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    val pressure: Long,
    val humidity: Long,

    @SerializedName("dew_point")
    val dewPoint: Double,

    val uvi: Double,
    val clouds: Long,
    val visibility: Long,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Long,

    @SerializedName("wind_gust")
    val windGust: Double,

    val weather: List<Weather>,
    val pop: Double? = null,
    val rain: Rain? = null
)

data class Rain(
    @SerializedName("1h")
    val the1H: Double
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,

    @SerializedName("moon_phase")
    val moonPhase: Double,

    val temp: Temp,

    @SerializedName("feels_like")
    val feelsLike: FeelsLike,

    val pressure: Long,
    val humidity: Long,

    @SerializedName("dew_point")
    val dewPoint: Double,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("wind_deg")
    val windDeg: Long,

    @SerializedName("wind_gust")
    val windGust: Double,

    val weather: List<Weather>,
    val clouds: Long,
    val pop: Double,
    val rain: Double? = null,
    val uvi: Double,
    val snow: Double? = null
)

data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)
