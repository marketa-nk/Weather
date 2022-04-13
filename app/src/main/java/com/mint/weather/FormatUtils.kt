package com.mint.weather

import kotlin.math.roundToInt

fun Double.toUiString(): String {
    return this.roundToInt().toString()
}

fun Long.toUiString(): String {
    return this.toDouble().roundToInt().toString()
}

fun Double.temperatureToString(): String {
    val s = "${this.toInt()}°C"
    return when {
        this >= 1 -> "+$s"
        else -> {
            s
        }
    }
}

/** this amount of precipitation is rounded up */
fun Double.precipitationToInt(): Int {
    return kotlin.math.ceil(this).toInt()
}