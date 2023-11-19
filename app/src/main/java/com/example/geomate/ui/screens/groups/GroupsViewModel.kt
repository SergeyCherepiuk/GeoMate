package com.example.geomate.ui.screens.groups

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geomate.data.models.Group
import com.example.geomate.data.repositories.GroupsRepository
import com.example.geomate.data.repositories.UsersRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val usersRepository: UsersRepository,
    private val groupsRepository: GroupsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    fun fetchGroups() = Firebase.auth.uid?.let { userId ->
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            groupsRepository.getAllAsFlow(userId).collect { groups ->
                _uiState.update {
                    it.copy(
                        groups = groups.associateWith { group ->
                            List(group.users.size) { Uri.EMPTY }
                        },
                        matchedGroups = groups,
                        isLoading = false,
                    )
                }
                _uiState.update {
                    it.copy(
                        groups = groups.associateWith { group ->
                            group.users.map { usersRepository.getProfilePicture(it) }
                        },
                    )
                }
            }
        }

        _uiState.update { it.copy(isLoading = false) }
    }

    fun updateSearchQuery(searchQuery: String) {
        val groups = uiState.value.groups.filterKeys { group ->
            group.name.contains(searchQuery, true)
        }.keys.toList()
        _uiState.update { it.copy(searchQuery = searchQuery, matchedGroups = groups) }
    }

    fun removeGroup(group: Group) = viewModelScope.launch {
        groupsRepository.remove(group)
    }
}