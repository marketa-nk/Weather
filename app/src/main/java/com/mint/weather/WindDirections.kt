package com.mint.weather

import java.lang.IllegalStateException

enum class WindDirections(
    val direction: String,
    val directionIcon: Int
) {

    NORTH("С", R.drawable.ic_baseline_north_24),
    NORTH_EAST("СВ", R.drawable.ic_baseline_north_east_24),
    EAST("В", R.drawable.ic_baseline_east_24),
    SOUTH_EAST("ЮВ", R.drawable.ic_baseline_south_east_24),
    SOUTH("Ю", R.drawable.ic_baseline_south_24),
    SOUTH_WEST("ЮЗ", R.drawable.ic_baseline_south_west_24),
    WEST("З", R.drawable.ic_baseline_west_24),
    NORTH_WEST("СЗ", R.drawable.ic_baseline_north_west_24);

    companion object {
        fun getWindDirection(windDeg: Long): WindDirections {
            return when (windDeg.toDouble()) {
                in 0.0..22.5 -> NORTH
                in 22.6..67.5 -> NORTH_EAST
                in 67.6..112.5 -> EAST
                in 112.6..157.5 -> SOUTH_EAST
                in 157.6..202.5 -> SOUTH
                in 202.6..247.5 -> SOUTH_WEST
                in 247.6..292.5 -> WEST
                in 292.6..337.5 -> NORTH_WEST
                in 337.6..360.0 -> NORTH
                else -> throw IllegalStateException("windDeg = $windDeg")
            }
        }
    }
}