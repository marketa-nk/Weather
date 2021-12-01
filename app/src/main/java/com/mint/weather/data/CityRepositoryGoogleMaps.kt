package com.mint.weather.data

import com.mint.weather.model.City
import com.mint.weather.network.googlemaps.CityNetworkGoogleMaps
import com.mint.weather.network.googlemaps.NetworkServiceGoogleMaps
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CityRepositoryGoogleMaps : CityRepository {

    private val networkService = NetworkServiceGoogleMaps().googleMapsApi

    override fun getCity(lat: Double, lon: Double, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val city: Call<CityNetworkGoogleMaps> = networkService.getCity("$lat,$lon")

        city.enqueue(object : Callback<CityNetworkGoogleMaps> {

            override fun onResponse(call: Call<CityNetworkGoogleMaps>, response: Response<CityNetworkGoogleMaps>) {
                val city = response.body()?.let { City(it.results[0].addressComponents[0].longName) }

                onSuccess(city!!.name)//todo
            }

            override fun onFailure(call: Call<CityNetworkGoogleMaps>, t: Throwable) {
                t.printStackTrace()
                onFailure(t)
            }
        })
    }
}