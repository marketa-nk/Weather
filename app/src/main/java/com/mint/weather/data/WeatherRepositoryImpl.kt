package com.mint.weather.data

import android.location.Location
import com.mint.weather.model.*
import com.mint.weather.network.NetworkService
import com.mint.weather.network.openweather.ActualWeather
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(networkService: NetworkService) : WeatherRepository {

    private val api = networkService.openWeatherApi

    override fun getWeather(location: Location): Single<Weather> {
        return api.getActualWeather(location.latitude, location.longitude)
            .map { response ->
                val weather = CurrentWeather(
                    response.current.temp,
                    response.current.weather[0].description.replaceFirstChar { c -> c.uppercase() },
                    response.current.feelsLike,
                    response.current.weather[0].icon,
                    response.current.windSpeed,
                    response.current.windDeg,
                    response.current.pressure,
                    response.current.humidity
                )
                val timezoneDiffMs = response.timezoneOffset * 1000 - Calendar.getInstance().timeZone.rawOffset
                Weather(
                    Date(response.current.dt * 1000 + timezoneDiffMs),
                    weather,
                    getHourlyWeather(response),
                    getDailyWeather(response)
                )
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

    private fun getDailyWeather(actualWeather: ActualWeather): List<DailyWeather> {
        return actualWeather.daily.map {
            val icon = it.weather[0].icon
            DailyWeather(
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

    override fun getCitiesWeather(cityList: List<City>, currentLocation: Location?): Observable<List<CityWeather>> {
        return Observable.fromIterable(cityList)
            .concatMapSingle { city ->
                getCityWeather(city, currentLocation)
                    .subscribeOn(Schedulers.io())
            }
            .toList()
            .toObservable()
    }

    private fun getCityWeather(city: City, currentLocation: Location?): Single<CityWeather> {
        val cityLocation = Location("loc").also {
            it.latitude = city.latitude
            it.longitude = city.longitude
        }
        return getWeather(cityLocation)
            .map { weather ->
                CityWeather(
                    city,
                    weather,
                    cityLocation.distanceToInKm(currentLocation)
                )
            }
    }

    private fun Location.distanceToInKm(loc: Location?): Double? {
        return if (loc == null) {
            null
        } else {
            (this.distanceTo(loc).toDouble() / 1000)
        }
    }
}