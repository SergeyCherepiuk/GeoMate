package com.example.geomate.model

import android.net.Uri

data class User(
    var email: String? = "",
    val username: String? = "",
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUri: Uri? = null,
    val bio: String = "",
)
