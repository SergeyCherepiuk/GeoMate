package com.example.geomate.data.models

import java.util.Date

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Date = Date(),
)