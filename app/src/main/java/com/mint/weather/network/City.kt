package com.mint.weather.network

import com.google.gson.annotations.SerializedName

data class City(
    val name: String,
    @SerializedName("local_names")
    val localNames: LocalNames,
    val lat: Double,
    val lon: Double,
    val country: String
){
    fun getDistance(latitude: Double, longitude: Double): Double{
        val distanceX = lat - latitude
        val distanceY = lon - longitude

        return kotlin.math.sqrt(distanceX * distanceX + distanceY * distanceY)
    }
}

data class LocalNames(
    @SerializedName("feature_name")
    val featureName: String,
    val ascii: String? = null,
    val ar: String? = null,
    val bg: String? = null,
    val ca: String? = null,
    val de: String? = null,
    val el: String? = null,
    val en: String? = null,
    val fa: String? = null,
    val fi: String? = null,
    val fr: String? = null,
    val gl: String? = null,
    val he: String? = null,
    val hi: String? = null,
    val id: String? = null,
    val it: String? = null,
    val ja: String? = null,
    val la: String? = null,
    val lt: String? = null,
    val pt: String? = null,
    val ru: String? = null,
    val sr: String? = null,
    val th: String? = null,
    val tr: String? = null,
    val vi: String? = null,
    val zu: String? = null,
    val af: String? = null,
    val az: String? = null,
    val da: String? = null,
    val eu: String? = null,
    val hr: String? = null,
    val hu: String? = null,
    val mk: String? = null,
    val nl: String? = null,
    val no: String? = null,
    val pl: String? = null,
    val ro: String? = null,
    val sk: String? = null,
    val sl: String? = null
)
