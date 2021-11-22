package com.mint.weather.model

data class Sunrise (
    override val dateTime: Long
    ): Time

data class Sunset (
    override val dateTime: Long
): Time