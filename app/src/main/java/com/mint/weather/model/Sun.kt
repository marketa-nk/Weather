package com.mint.weather.model

import java.util.*

data class Sunrise (
    override val date: Date,
    ): Time

data class Sunset (
    override val date: Date,
): Time