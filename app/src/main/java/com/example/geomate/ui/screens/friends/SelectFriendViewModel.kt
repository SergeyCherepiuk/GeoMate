package com.example.geomate.ui.screens.friends

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import com.example.geomate.data.models.User
import com.example.geomate.data.repositories.GroupsRepository
import com.example.geomate.data.repositories.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectFriendViewModel(
    private val usersRepository: UsersRepository,
    private val groupsRepository: GroupsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendsUiState())
    override val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    override val icon: ImageVector = Icons.Outlined.Add

    override fun fetchFriends() {
        _uiState.update { it.copy(isLoading = true) }
        // TODO: Fetch all friends that are not already in the group (from uiState)
        Log.d("asdqwe", "fetchFriends: Fetching friends that are not in the group")
        _uiState.update { it.copy(isLoading = false) }
    }

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
        return true // TODO: Implement
    }

    override fun setGroupById(groupId: String) {
        viewModelScope.launch {
            groupsRepository.get(groupId).collect { group ->
                _uiState.update { it.copy(group = group) }
            }
        }
    }

    override fun onIconButton(friend: User) {
        // TODO: Add friend to a group
        Log.d("asdqwe", "onIconButton: Add friend to a group")
    }
}