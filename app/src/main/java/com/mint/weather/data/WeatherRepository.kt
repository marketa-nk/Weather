package com.mint.weather.data

import com.mint.weather.model.*
import com.mint.weather.network.ActualWeather
import com.mint.weather.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class WeatherRepository {

    private val networkService = NetworkService().actualWeatherApi

    fun getWeatherNow(lat: Double, lon: Double, onSuccess: (WeatherMain, List<Time>, List<DailyWeatherShort>) -> Unit) {
        val actualWeather: Call<ActualWeather> = networkService.getActualWeather(lat, lon)

        actualWeather.enqueue(object : Callback<ActualWeather> {
            override fun onResponse(call: Call<ActualWeather>, response: Response<ActualWeather>) {

                val actualWeather = response.body()
                if (actualWeather != null) {
                    val weather = WeatherMain(
                        actualWeather.current.temp,
                        actualWeather.current.weather[0].description.replaceFirstChar { c -> c.uppercase() },
                        actualWeather.current.feelsLike,
                        actualWeather.current.weather[0].icon,
                        actualWeather.current.windSpeed,
                        actualWeather.current.windDeg,
                        actualWeather.current.pressure,
                        actualWeather.current.humidity
                    )
                    onSuccess(weather, getHourlyWeather(actualWeather), getDailyWeather(actualWeather))
                }
            }

            override fun onFailure(call: Call<ActualWeather>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun getHourlyWeather(actualWeather: ActualWeather): List<Time> {
        val now = Date()
        val twoDays = Date(System.currentTimeMillis() + 172800000) //todo
        val hourlyWeather = actualWeather.hourly.map { HourWeather(Date(it.dt * 1000), it.temp, it.weather[0].icon) }
        val sunrises = actualWeather.daily.map { daily -> Sunrise(Date(daily.sunrise * 1000)) }.filter { it.date > now && it.date < twoDays }
        val sunsets = actualWeather.daily.map { daily -> Sunset(Date(daily.sunset * 1000)) }.filter { it.date > now && it.date < twoDays }
        val list = sunrises + sunsets + hourlyWeather
        return list.sortedBy { it.date }
    }

    private fun getDailyWeather(actualWeather: ActualWeather): List<DailyWeatherShort> {
        return actualWeather.daily.map {
            DailyWeatherShort(
                Date(it.dt * 1000),
                it.temp.day,
                it.temp.night,
                it.weather[0].icon,
                it.rain,
                it.windSpeed,
                WindDirections.getWindDirection(it.windDeg),
                it.windGust
            )
        }
    }
}