package com.example.geomate.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.geomate.R
import com.example.geomate.statemachine.FriendshipState
import com.example.geomate.ui.components.ProfileButtonsRow
import com.example.geomate.ui.components.ProfileInfo
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.theme.spacing
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.fresco.FrescoImage

fun NavGraphBuilder.profile(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    composable(
        route = "${Destinations.PROFILE_ROUTE}/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.StringType })
    ) { backStackEntry ->
        val uiState by viewModel.uiState.collectAsState()
        ProfileScreen(
            userId = backStackEntry.arguments?.getString("userId") ?: "",
            uiState = uiState,
            viewModel = viewModel,
            navController = navController,
        )
    }
}

fun NavController.navigateToProfile(userId: String) {
    navigate("${Destinations.PROFILE_ROUTE}/$userId") {
        launchSingleTop = true
    }
}

data class DropdownMenuItem(
    val icon: ImageVector,
    @StringRes val textId: Int,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.fetchProfilePicture(userId)
        viewModel.fetchUser(userId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_profile),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.updateIsMenuVisible(!uiState.isLoading) }) {
                        Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = uiState.isMenuVisible,
                        onDismissRequest = { viewModel.updateIsMenuVisible(false) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    ) {
                        val dropdownMenuItems = listOf(
                            DropdownMenuItem(Icons.Outlined.Edit, R.string.profile_edit_profile) { /* TODO: Navigate to the "Edit profile" screen */ },
                            DropdownMenuItem(Icons.Outlined.CameraAlt, R.string.profile_change_picture) { /* TODO: Open image picker */ },
                            DropdownMenuItem(
                                icon = if (isSystemInDarkTheme()) Icons.Outlined.WbSunny else Icons.Outlined.DarkMode,
                                textId = if (isSystemInDarkTheme()) R.string.profile_light_mode else R.string.profile_dark_mode
                            ) {
                                // TODO: Toggle UI mode
                            },
                            DropdownMenuItem(Icons.Outlined.LockReset, R.string.profile_reset_password) { /* TODO: Reset password */ },
                            DropdownMenuItem(Icons.Outlined.ExitToApp, R.string.profile_log_out) { /* TODO: Log out */ },
                        )

                        dropdownMenuItems.forEach {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                                    ) {
                                        Icon(
                                            imageVector = it.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = stringResource(id = it.textId),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onBackground,
                                        )
                                    }
                                },
                                onClick = it.onClick
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = MaterialTheme.spacing.extraSmall)
            )
        }
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(it)
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraLarge),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(it)
                    .padding(30.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val drawableId =
                        if (isSystemInDarkTheme()) R.drawable.profile_picture_placeholder_dark
                        else R.drawable.profile_picture_placeholder_light

                    FrescoImage(
                        imageUrl = uiState.profilePictureUri.toString(),
                        failure = {
                            Image(painter = painterResource(id = drawableId), contentDescription = null)
                        },
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .size(165.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = "${uiState.user.firstName} ${uiState.user.lastName}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }

                // TODO: Get friendship status from a bunch of requests
                if (Firebase.auth.uid != userId) {
                    ProfileButtonsRow(friendshipState = FriendshipState.AcceptedWithoutNotifications)
                }

                ProfileInfo(uiState.user)
            }
        }
    }
}