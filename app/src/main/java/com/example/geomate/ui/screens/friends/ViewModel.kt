package com.example.geomate.ui.screens.friends

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.geomate.data.models.User
import kotlinx.coroutines.flow.StateFlow

abstract class ViewModel : ViewModel() {
    abstract val uiState: StateFlow<FriendsUiState>
    abstract val icon: ImageVector
    abstract fun onIconButton(friend: User)
    abstract fun fetchFriends()
    abstract fun updateSearchQuery(searchQuery: String)
    abstract fun setGroupById(groupId: String)
}