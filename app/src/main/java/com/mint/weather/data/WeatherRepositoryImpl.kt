package com.mint.weather.data

import com.mint.weather.model.*
import com.mint.weather.network.NetworkService
import com.mint.weather.network.openweather.ActualWeather
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(networkService: NetworkService) : WeatherRepository {

    private val api = networkService.openWeatherApi

    override fun getWeatherNow(location: Location): Single<Triple<WeatherMain, List<Time>, List<DailyWeatherShort>>> {
        return api.getActualWeather(location.lat, location.lon)
            .map { response ->
                val icon = response.current.weather[0].icon
                val weather = WeatherMain(
                    response.current.temp,
                    response.current.weather[0].description.replaceFirstChar { c -> c.uppercase() },
                    response.current.feelsLike,
                    icon,
                    getOpenWeatherIconUrl(icon),
                    response.current.windSpeed,
                    response.current.windDeg,
                    response.current.pressure,
                    response.current.humidity
                )
                Triple(weather, getHourlyWeather(response), getDailyWeather(response))
            }
    }


    private fun getHourlyWeather(actualWeather: ActualWeather): List<Time> {
        val timezoneDiff = actualWeather.timezoneOffset * 1000 - Calendar.getInstance().timeZone.rawOffset //millisec
        val now = Date(Date().time + timezoneDiff)
        val hourlyWeather = actualWeather.hourly.map {
            val icon = it.weather[0].icon
            HourWeather(
                date = Date(it.dt * 1000 + timezoneDiff),
                temp = it.temp,
                icon = icon,
                iconUrl = getOpenWeatherIconUrl(icon),
            )
        }
        val twoDays = Calendar.getInstance().apply { add(Calendar.DATE, 2) }.time
        val filterNextTwoDay: (Time) -> Boolean = { it.date > now && it.date < twoDays }
        val sunrises = actualWeather.daily.map { daily -> Sunrise(Date(daily.sunrise * 1000 + timezoneDiff)) }.filter(filterNextTwoDay)
        val sunsets = actualWeather.daily.map { daily -> Sunset(Date(daily.sunset * 1000 + timezoneDiff)) }.filter(filterNextTwoDay)
        return (sunrises + sunsets + hourlyWeather).sortedBy { it.date }
    }

    private fun getDailyWeather(actualWeather: ActualWeather): List<DailyWeatherShort> {
        return actualWeather.daily.map {
            val icon = it.weather[0].icon
            DailyWeatherShort(
                Date(it.dt * 1000),
                it.temp.day,
                it.temp.night,
                icon,
                getOpenWeatherIconUrl(icon),
                it.rain,
                it.snow,
                it.windSpeed,
                WindDirections.getWindDirection(it.windDeg),
                it.windGust
            )
        }
    }
}