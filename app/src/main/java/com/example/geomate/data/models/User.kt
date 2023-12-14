package com.example.geomate.data.models

import com.google.android.gms.maps.model.LatLng
import java.util.Date

data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val bio: String = "",
    val location: Location = Location(0.0, 0.0),
    val joined: Date = Date(),
)
