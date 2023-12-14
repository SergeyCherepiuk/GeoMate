package com.example.geomate.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geomate.data.models.FriendshipStatus.Accepted
import com.example.geomate.data.models.FriendshipStatus.Sent
import com.example.geomate.data.repositories.FriendshipRepository
import com.example.geomate.data.repositories.UsersRepository
import com.example.geomate.statemachine.FriendshipState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val usersRepository: UsersRepository,
    private val friendshipRepository: FriendshipRepository,
) : ViewModel() {
    private var fetchingUserJob: Job? = null
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun fetchProfilePicture(userId: String) = viewModelScope.launch {
        _uiState.update { it.copy(profilePictureUri = usersRepository.getProfilePicture(userId)) }
    }

    fun startFetchingUser(userId: String) {
        fetchingUserJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            usersRepository.getSingleAsFlow(userId).collect { userOrNull ->
                userOrNull?.let { user ->
                    _uiState.update {
                        it.copy(user = user, isLoading = false)
                    }
                }
            }
        }
    }

    fun stopFetchingUser() {
        fetchingUserJob?.cancel()
        fetchingUserJob = null
    }

    fun fetchFriendship(userId: String) = viewModelScope.launch {
        friendshipRepository.getAsFlow(userId).collect { friendshipOrNull ->
            _uiState.update {
                it.copy(friendshipRequest = friendshipOrNull)
            }
        }
    }

    fun updateIsMenuVisible(isMenuVisible: Boolean) {
        _uiState.update { it.copy(isMenuVisible = isMenuVisible) }
    }

    fun updateProfilePictureUri(profilePictureUri: Uri?) {
        _uiState.update { it.copy(profilePictureUri = profilePictureUri) }
        onChangePictureClick()
    }

    fun onResetPasswordClick() = viewModelScope.launch {
        try {
            usersRepository.sendRecoveryEmail(uiState.value.user.email)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onChangePictureClick() = viewModelScope.launch {
        val uid = Firebase.auth.uid.toString()
        try {
            uiState.value.profilePictureUri?.let {
                usersRepository.addProfilePicture(uid, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createFriendshipState(): FriendshipState {
        val request = _uiState.value.friendshipRequest
        val isCurrentUserSender = request?.senderId == Firebase.auth.uid
        val isCurrentUserRecipient = request?.recipientId == Firebase.auth.uid

        return when {
            request == null -> FriendshipState.None(friendshipRepository)
            isCurrentUserSender && request.status == Sent -> FriendshipState.SentByMe(
                friendshipRepository
            )

            isCurrentUserRecipient && request.status == Sent -> FriendshipState.SentByUser(
                friendshipRepository
            )

            request.status == Accepted -> {
                if (isCurrentUserRecipient && request.recipientHasNotifications || isCurrentUserSender && request.senderHasNotifications)
                    FriendshipState.AcceptedWithNotifications(friendshipRepository)
                else
                    FriendshipState.AcceptedWithoutNotifications(friendshipRepository)
            }

            else -> FriendshipState.None(friendshipRepository)
        }
    }
}