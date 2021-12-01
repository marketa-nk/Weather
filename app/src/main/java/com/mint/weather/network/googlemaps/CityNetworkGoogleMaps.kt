package com.mint.weather.network.googlemaps

import com.google.gson.annotations.SerializedName


data class CityNetworkGoogleMaps (
    val results: List<Result>
)

data class Result (
    @SerializedName ("address_components")
    val addressComponents: List<AddressComponent>,

    @SerializedName ("formatted_address")
    val formattedAddress: String,

    val geometry: Geometry,

    @SerializedName ("place_id")
    val placeID: String,

    val types: List<String>
)

data class AddressComponent (
    @SerializedName ("long_name")
    val longName: String,

    @SerializedName ("short_name")
    val shortName: String,

    val types: List<Type>
)

enum class Type(val value: String) {
    AdministrativeAreaLevel1("administrative_area_level_1"),
    AdministrativeAreaLevel2("administrative_area_level_2"),
    Country("country"),
    Locality("locality"),
    PlusCode("plus_code"),
    Political("political"),
    Route("route"),
    StreetNumber("street_number"),
    Sublocality("sublocality"),
    SublocalityLevel1("sublocality_level_1");

    companion object {
        public fun fromValue(value: String): Type = when (value) {
            "administrative_area_level_1" -> AdministrativeAreaLevel1
            "administrative_area_level_2" -> AdministrativeAreaLevel2
            "country"                     -> Country
            "locality"                    -> Locality
            "plus_code"                   -> PlusCode
            "political"                   -> Political
            "route"                       -> Route
            "street_number"               -> StreetNumber
            "sublocality"                 -> Sublocality
            "sublocality_level_1"         -> SublocalityLevel1
            else                          -> throw IllegalArgumentException()
        }
    }
}

data class Geometry (
    val bounds: Bounds,
    val location: Location,

    @SerializedName ("location_type")
    val locationType: String,

    val viewport: Bounds,
)

data class Bounds (
    val northeast: Location,
    val southwest: Location
)

data class Location (
    val lat: Double,
    val lng: Double
)


