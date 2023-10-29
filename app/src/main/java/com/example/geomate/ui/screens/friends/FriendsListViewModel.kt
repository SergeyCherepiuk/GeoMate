package com.example.geomate.ui.screens.friends

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.example.geomate.data.models.User
import com.example.geomate.data.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsListViewModel(
    private val usersRepository: UsersRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendsUiState())
    override val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    override val icon: ImageVector = Icons.Outlined.Close

    override fun onIconButton(friend: User) {
        viewModelScope.launch {
            // TODO: Remove friendship request
            Log.d("asdqwe", "onIconButton: Remove friend")
        }
    }

    override fun fetchFriends() {
        Log.d("asdqwe", "fetchFriends: Fetching all friends")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO:
            //  1. Replace this call with query for "friendsRequests" collection
            //  2. Make the call to return a List<User> instead of Flow<List<User>>
            usersRepository.getAll(
                listOf(
                    "JZzebDr8JUQlkWaHBSp9hzC3qMO2",
                    "ItkRKNiOOzXzvJoYrsT8x6DtZxp2",
                    "kuX1hqvxNUZVE09SrNYeS35tWqE2",
                    "mOgWAcJxw6bVe71WXoqj6VTb5sj1",
                )
            ).collect { friends ->
                val friendsWithProfilePictures = friends.associateWith { friend ->
                    usersRepository.getProfilePicture(friend.uid)
                }
                _uiState.update {
                    it.copy(
                        friends = friendsWithProfilePictures,
                        matchedFriends = friendsWithProfilePictures.filter { pair ->
                            filter(pair.key, uiState.value.searchQuery)
                        },
                        isLoading = false,
                    )
                }
            }
        }
    }

    override fun setGroupById(groupId: String) { }

    override fun updateSearchQuery(searchQuery: String) {
        _uiState.update {
            it.copy(
                searchQuery = searchQuery,
                matchedFriends = uiState.value.friends.filter { pair ->
                    filter(pair.key, searchQuery)
                }
            )
        }
    }

    private fun filter(friend: User, searchQuery: String): Boolean {
        return listOf(friend.firstName, friend.lastName, friend.username).any {
            it.contains(searchQuery)
        }
    }
}