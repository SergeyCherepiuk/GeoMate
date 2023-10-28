package com.example.geomate.ui.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geomate.data.models.User
import com.example.geomate.data.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val usersRepository: UsersRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    fun fetchFriends() = viewModelScope.launch {
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

    fun updateSearchQuery(searchQuery: String) {
        _uiState.update {
            it.copy(
                searchQuery = searchQuery,
                matchedFriends = uiState.value.friends.filter { pair ->
                    filter(pair.key, searchQuery)
                }
            )
        }
    }

    private fun filter(friends: User, searchQuery: String): Boolean {
        return listOf(friends.firstName, friends.lastName, friends.username).any {
            it.contains(searchQuery)
        }
    }
}