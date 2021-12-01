package com.mint.weather.data

import com.mint.weather.model.City
import com.mint.weather.network.openweather.CityNetwork
import com.mint.weather.network.openweather.NetworkServiceOpenWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CityRepositoryOpenWeather: CityRepository {

    private val networkService = NetworkServiceOpenWeather().actualOpenWeatherApi

    override fun getCity(lat: Double, lon: Double, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val cities: Call<List<CityNetwork>> = networkService.getCity(lat, lon)

        cities.enqueue(object : Callback<List<CityNetwork>> {
            override fun onResponse(call: Call<List<CityNetwork>>, response: Response<List<CityNetwork>>) {

                val city = response.body()?.firstOrNull()?.let { City(it.localNames.ru ?: it.name) }

//                val city = response.body()?.firstOrNull()
//                    .let { it?.localNames?.ru ?: it?.name }
//                    .orEmpty()
//                val citiesList = response.body()?.map { City(it.name, it.localNames, it.lat, it.lon, it.country) }
//                val citY = response.body()?.map { City(it.name, it.localNames, it.lat, it.lon, it.country) }?.minByOrNull { it.getDistance(lat, lon) }.let { it?.localNames?.ru ?: it?.name }
//                    .orEmpty()

                onSuccess(city!!.name)//todo
            }

            override fun onFailure(call: Call<List<CityNetwork>>, t: Throwable) {
                t.printStackTrace()
                onFailure(t)
            }
        })
    }
}