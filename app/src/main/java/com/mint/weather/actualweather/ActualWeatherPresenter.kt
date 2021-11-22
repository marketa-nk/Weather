package com.mint.weather.actualweather

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mint.weather.model.*
import com.mint.weather.network.ActualWeather
import com.mint.weather.network.CityNetwork
import com.mint.weather.network.WeatherApi
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@InjectViewState
class ActualWeatherPresenter : MvpPresenter<WeatherView?>() {

    private val actualWeatherApi: WeatherApi by lazy { getActualWeatherApi("https://api.openweathermap.org/") }


    fun sendBaseRequests(lat: Double, lon: Double) {
        getWeatherNow(lat, lon)
        getCity(lat, lon) {
            viewState?.setCityName(it)
        }
    }

    private fun getCity(lat: Double, lon: Double, onSuccess: (String) -> Unit) {
        val cities: Call<List<CityNetwork>> = actualWeatherApi.getCity(lat, lon)

        cities.enqueue(object : Callback<List<CityNetwork>> {
            override fun onResponse(call: Call<List<CityNetwork>>, response: Response<List<CityNetwork>>) {

                val cityNetwork = response.body()?.firstOrNull()
                val city = cityNetwork?.let { City(it.localNames.ru ?: it.name) }

//                val city = response.body()?.firstOrNull()
//                    .let { it?.localNames?.ru ?: it?.name }
//                    .orEmpty()
//                val citiesList = response.body()?.map { City(it.name, it.localNames, it.lat, it.lon, it.country) }
//                val citY = response.body()?.map { City(it.name, it.localNames, it.lat, it.lon, it.country) }?.minByOrNull { it.getDistance(lat, lon) }.let { it?.localNames?.ru ?: it?.name }
//                    .orEmpty()

                onSuccess(city!!.name)//todo
            }

            override fun onFailure(call: Call<List<CityNetwork>>, t: Throwable?) {}
        })
    }

    private fun getWeatherNow(lat: Double, lon: Double) {
        val actualWeather: Call<ActualWeather> = actualWeatherApi.getActualWeather(lat, lon)

        actualWeather.enqueue(object : Callback<ActualWeather> {
            override fun onResponse(call: Call<ActualWeather>, response: Response<ActualWeather>) {

                val actualWeather = response.body()
                if (actualWeather != null) {
                    viewState?.showWeather(
                        WeatherMain(
                            actualWeather.current.temp,
                            actualWeather.current.weather[0].description.replaceFirstChar { c -> c.uppercase() },
                            actualWeather.current.feelsLike,
                            actualWeather.current.weather[0].icon,
                            actualWeather.current.windSpeed,
                            actualWeather.current.windDeg,
                            actualWeather.current.pressure,
                            actualWeather.current.humidity
                        ),
                        getHourlyWeather(actualWeather),
                        getDailyWeather(actualWeather)
                    )
                }
            }

            override fun onFailure(call: Call<ActualWeather>, t: Throwable) {
            }
        })
    }

    private fun getHourlyWeather(actualWeather: ActualWeather): List<Time> {
        val now = Date(System.currentTimeMillis())
        val twoDays = Date(System.currentTimeMillis() + 172800000)
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


    private fun getActualWeatherApi(url: String): WeatherApi {
        val build = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request()

                val newUrl: HttpUrl = request.url()
                    .newBuilder()
                    .addQueryParameter("appid", "b7044fa387aaefecbb6a8888f3624867")
                    .build()

                val newRequest = request.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApi::class.java)
    }

}
