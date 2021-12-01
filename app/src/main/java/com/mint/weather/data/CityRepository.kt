package com.mint.weather.data

interface CityRepository {
    fun getCity(lat: Double, lon: Double, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit)
}