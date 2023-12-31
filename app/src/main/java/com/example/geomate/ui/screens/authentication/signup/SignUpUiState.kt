package com.example.geomate.ui.screens.authentication.signup

import android.net.Uri

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val profilePictureUri: Uri? = null,
    val bio: String = "",
    val isFirstNameValid: Boolean = true,
    val isLastNameValid: Boolean = true,
    val isUsernameValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
)