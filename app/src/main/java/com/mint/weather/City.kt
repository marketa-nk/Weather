package com.mint.weather

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lng: Double,
)