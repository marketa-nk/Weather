package com.mint.weather.model

import java.io.Serializable

data class CityWeather(
    val city: City,
    val weather: Weather,
    val distanceFromCurrentPlace: Double?,
) : FavoritesItem

data class CurrentCityWeather(
    val city: City?,
    val currentWeather: CurrentWeather?
): FavoritesItem, Serializable

data class FavoritesText(
    val text: String
) : FavoritesItem


interface FavoritesItem