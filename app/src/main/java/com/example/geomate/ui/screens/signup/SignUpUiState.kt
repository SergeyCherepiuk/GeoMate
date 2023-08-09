package com.example.geomate.ui.screens.signup

import android.net.Uri

data class SignUpUiState(
    val email: String = "dd22@gmail.com",
    val password: String = "Ddd2222!",
    val firstName: String = "Dddd",
    val lastName: String = "Bbbb",
    val username: String = "asdkoda",
    val profilePictureUri: Uri? = null,
    val bio: String = "",
)