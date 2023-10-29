package com.example.geomate.ui.screens.friends

import android.net.Uri
import com.example.geomate.data.models.Group
import com.example.geomate.data.models.User

data class FriendsUiState(
    val isLoading: Boolean = true,
    val friends: Map<User, Uri> = mapOf(),
    val matchedFriends: Map<User, Uri> = mapOf(),
    val searchQuery: String = "",
    val group: Group? = null,
)
