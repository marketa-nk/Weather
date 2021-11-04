package com.mint.weather

data class Sunrise (
    override val dateTime: Long
    ):Time

data class Sunset (
    override val dateTime: Long
): Time