package com.mint.weather.data

import com.mint.weather.model.City
import com.mint.weather.model.Location

import com.mint.weather.network.googlemaps.NetworkServiceGoogleMaps
import io.reactivex.Observable


class GoogleMapsCityRepository : CityRepository {

    private val networkService = NetworkServiceGoogleMaps().googleMapsApi

    override fun getCity(location: Location): Observable<City> {
        return networkService.getCity("${location.lat},${location.lon}")
            .map { City(it.results[0].addressComponents[0].longName) }
    }
}
