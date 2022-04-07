package com.mint.weather.data

import com.mint.weather.model.*
import com.mint.weather.network.NetworkService
import com.mint.weather.network.openweather.ActualWeather
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val networkService: NetworkService) : WeatherRepository {

    private val api = networkService.openWeatherApi

    override fun getWeatherNow(location: Location): Single<Triple<WeatherMain, List<Time>, List<DailyWeatherShort>>> {
        return api.getActualWeather(location.lat, location.lon)
            .map { response ->
                val weather = WeatherMain(
                    response.current.temp,
                    response.current.weather[0].description.replaceFirstChar { c -> c.uppercase() },
                    response.current.feelsLike,
                    response.current.weather[0].icon,
                    response.current.windSpeed,
                    response.current.windDeg,
                    response.current.pressure,
                    response.current.humidity
                )
                Triple(weather, getHourlyWeather(response), getDailyWeather(response))
            }
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
                it.snow,
                it.windSpeed,
                WindDirections.getWindDirection(it.windDeg),
                it.windGust
            )
        }
    }
}