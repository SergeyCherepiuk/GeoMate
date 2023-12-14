package com.example.geomate.data.models

data class Location(
    val latitude: Double,
    val longitude: Double,
) { constructor() : this(0.0, 0.0) }