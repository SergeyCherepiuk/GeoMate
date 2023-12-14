package com.example.geomate.ui.screens.friends

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.geomate.R
import com.example.geomate.ui.components.GeoMateTextField
import com.example.geomate.ui.components.TextFieldIcon
import com.example.geomate.ui.components.bottomnavbar.BottomNavigationBar
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.screens.friends.components.Friend
import com.example.geomate.ui.screens.groups.navigateToGroups
import com.example.geomate.ui.screens.map.navigateToMap
import com.example.geomate.ui.screens.profile.navigateToProfile
import com.example.geomate.ui.theme.spacing

fun NavGraphBuilder.friendsList(
    viewModel: FriendsViewModel,
    navController: NavController,
) {
    composable(Destinations.SOCIAL_ROUTE) {
        val uiState by viewModel.uiState.collectAsState()
        FriendsScreen(
            uiState = uiState,
            viewModel = viewModel,
            navController = navController,
        )
    }
}

fun NavController.navigateToFriends() {
    navigate(Destinations.SOCIAL_ROUTE) {
        launchSingleTop = true
    }
}

@Composable
fun FriendsScreen(
    uiState: FriendsUiState,
    viewModel: FriendsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        viewModel.fetchFriends()
    }

    Scaffold(
        topBar = {
            GeoMateTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                leadingIcons = listOf(
                    TextFieldIcon {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = it,
                        )
                    }
                ),
                placeholder = stringResource(id = R.string.friends_search_placeholder),
                imeAction = ImeAction.Search,
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Destinations.SOCIAL_ROUTE,
                navigateToMap = navController::navigateToMap,
                navigateToGroups = navController::navigateToGroups,
                navigateToSocial = { },
            )
        },
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            LazyColumn {
                itemsIndexed(uiState.matchedFriends) { index, friend ->
                    Friend(
                        friend = friend,
                        profilePicture = uiState.friends[friend] ?: Uri.EMPTY,
                        onSelect = { navController.navigateToProfile(friend.uid) },
                        onRemove = viewModel::removeFriend
                    )
                    if (index < uiState.friends.size - 1) {
                        Divider(color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}