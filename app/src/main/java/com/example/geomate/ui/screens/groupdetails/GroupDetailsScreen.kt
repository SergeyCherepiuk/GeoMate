package com.example.geomate.ui.screens.groupdetails

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.geomate.R
import com.example.geomate.ui.components.GeoMateFAB
import com.example.geomate.ui.components.GeoMateTextField
import com.example.geomate.ui.components.GroupUserRow
import com.example.geomate.ui.components.TextFieldIcon
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.screens.friends.navigateToSelectFriend
import com.example.geomate.ui.screens.profile.navigateToProfile
import com.example.geomate.ui.theme.spacing

fun NavGraphBuilder.groupDetails(
    viewModel: GroupDetailsViewModel,
    navController: NavController
) {
    composable(
        route = "${Destinations.GROUP_DETAILS_ROUTE}/{groupId}",
        arguments = listOf(navArgument("groupId") { type = NavType.StringType })
    ) { backStackEntry ->
        val uiState by viewModel.uiState.collectAsState()
        GroupDetailsScreen(
            groupId = backStackEntry.arguments?.getString("groupId") ?: "",
            uiState = uiState,
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavController.navigateToGroupDetails(groupId: String) {
    navigate("${Destinations.GROUP_DETAILS_ROUTE}/$groupId") {
        launchSingleTop = true
    }
}

@Composable
fun GroupDetailsScreen(
    groupId: String,
    uiState: GroupDetailsUiState,
    viewModel: GroupDetailsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(groupId) {
        viewModel.fetchGroup(groupId)
    }

    Scaffold(
        topBar = {
            GeoMateTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                leadingIcons = listOf(
                    TextFieldIcon {
                        Icon(
                            imageVector = Icons.Outlined.Group,
                            contentDescription = null,
                            modifier = it,
                        )
                    }
                ),
                placeholder = stringResource(id = R.string.group_details_name_placeholder),
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                GeoMateFAB(
                    icon = Icons.Outlined.Add,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = 2.dp
                ) { navController.navigateToSelectFriend(groupId) }
                AnimatedVisibility (uiState.users.isNotEmpty() && uiState.name.isNotBlank()) {
                    GeoMateFAB(
                        icon = Icons.Outlined.Check,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        elevation = 2.dp
                    ) { /* TODO: Save and navigate back */ }
                }
            }
        },
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            itemsIndexed(uiState.users.keys.toList()) { index, user ->
                GroupUserRow(
                    user = user to (uiState.users[user] ?: Uri.EMPTY),
                    onSelect = { navController.navigateToProfile(it.uid) },
                    onRemove = { viewModel.removeUser(groupId, it.uid) }
                )
                if (index < uiState.users.size - 1) {
                    Divider(color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}