package com.example.geomate.ui.screens.signup

import android.net.Uri
import com.example.geomate.service.account.Authentication
import com.example.geomate.service.storage.StorageService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

interface SignUpViewModel {
    val storageService: StorageService
    val uiState: StateFlow<SignUpUiState>
    fun updateEmail(email: String)
    fun updatePassword(password: String)
    fun updateFirstName(firstName: String)
    fun updateLastName(lastName: String)
    fun updateUsername(username: String)
    fun updateBio(bio: String)
    fun updateProfilePictureUri(profilePictureUri: Uri?)
    fun updateIsFirstNameValid(isFirstNameValid: Boolean)
    fun updateIsLastNameValid(isLastNameValid: Boolean)
    fun updateIsUsernameValid(isUsernameValid: Boolean)
    fun updateIsEmailValid(isEmailValid: Boolean)
    fun updateIsPasswordValid(isPasswordValid: Boolean)
    suspend fun signUp(authentication: Authentication): FirebaseUser?
}