package com.example.geomate.ui.screens.signup

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.geomate.model.User
import com.example.geomate.service.AccountService
import com.example.geomate.service.StorageService
import com.example.geomate.ui.screens.GeoMateViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel : GeoMateViewModel() {
    private val accountService = AccountService(FirebaseAuth.getInstance())
    private val storageService = StorageService(FirebaseFirestore.getInstance())
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password
    private val firstName
        get() = uiState.value.firstName
    private val lastName
        get() = uiState.value.lastName
    private val username
        get() = uiState.value.username
    private val bio
        get() = uiState.value.bio
    private val profilePictureUri
        get() = uiState.value.profilePictureUri

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun updateFirstName(firstName: String) {
        _uiState.update { it.copy(firstName = firstName) }
    }

    fun updateLastName(lastName: String) {
        _uiState.update { it.copy(lastName = lastName) }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updateBio(bio: String) {
        _uiState.update { it.copy(bio = bio) }
    }

    fun updateProfilePictureUri(profilePictureUri: Uri?) {
        _uiState.update { it.copy(profilePictureUri = profilePictureUri) }
    }

    fun onSignUpClick(): Boolean {
        viewModelScope.launch {
            storageService.addUser(
                User(
                    email = email,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    profilePictureUri = profilePictureUri,
                    bio = bio
                )
            )
            launchCatching(block = {
                accountService.signUp(email, password)
            })
        }
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun isUsernameTaken(username: String): Boolean {
        var result = false
        launchCatching {
            result = storageService.checkIfUsernameTaken(username)
        }
        return result
    }
}