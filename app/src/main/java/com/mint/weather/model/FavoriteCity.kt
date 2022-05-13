package com.mint.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteCity(
    @PrimaryKey
    val cityId: String,
    val name: String,
    val lat : Double,
    val lng : Double,
)

