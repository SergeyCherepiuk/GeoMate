package com.example.geomate.ui.screens.signup

import android.net.Uri

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val profilePictureUri: Uri? = null,
    val bio: String = "",
)