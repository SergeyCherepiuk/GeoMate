package com.example.geomate.service.google

import android.service.autofill.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
)
