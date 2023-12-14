package com.example.geomate.ui.screens.map

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.geomate.R
import com.example.geomate.ui.components.GeoMateFAB
import com.example.geomate.ui.components.GeoMateTextField
import com.example.geomate.ui.components.IconWithNotification
import com.example.geomate.ui.components.TextFieldIcon
import com.example.geomate.ui.components.bottomnavbar.BottomNavigationBar
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.screens.friends.navigateToFriends
import com.example.geomate.ui.screens.groups.navigateToGroups
import com.example.geomate.ui.screens.map.components.Chips
import com.example.geomate.ui.screens.map.components.Marker
import com.example.geomate.ui.screens.notifications.navigateToNotifications
import com.example.geomate.ui.screens.profile.navigateToProfile
import com.example.geomate.ui.screens.search.navigateToSearch
import com.example.geomate.ui.theme.spacing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.fresco.FrescoImage


fun NavGraphBuilder.map(
    viewModel: MapViewModel,
    navController: NavController,
) {
    composable(Destinations.MAP_ROUTE) {
        val uiState by viewModel.uiState.collectAsState()
        MapScreen(
            uiState = uiState,
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavController.navigateToMap() {
    popBackStack()
    navigate(Destinations.MAP_ROUTE) {
        launchSingleTop = true
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    viewModel: MapViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Firebase.auth.uid) {
        Firebase.auth.uid?.let {
            viewModel.fetchProfilePicture(it)
            viewModel.fetchGroups(it)
            viewModel.fetchNumberOfNotifications()
        }
    }

    PermissionsRequired(
        multiplePermissionsState = multiplePermissionsState,
        permissionsNotGrantedContent = {
            SideEffect {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }

            // TODO: Display oopsy daisy map
        },
        permissionsNotAvailableContent = { /* ... */ }
    ) {
        Map(
            uiState = uiState,
            viewModel = viewModel,
            navController = navController,
            modifier = modifier,
        )
    }
}

@Composable
fun Map(
    uiState: MapUiState,
    viewModel: MapViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapStyleId = if (isSystemInDarkTheme()) R.raw.map_style_dark else R.raw.map_style_light

    LaunchedEffect(Unit) {
        viewModel.startMonitoringUserLocation()
    }

    LifecycleStartEffect(Unit) {
        viewModel.startFetchingFriendsLocation()
        onStopOrDispose { viewModel.stopFetchingFriendsLocation() }
    }

    Scaffold(
        floatingActionButton = {
            GeoMateFAB(
                icon = Icons.Outlined.GpsFixed,
                onClick = viewModel::pointCameraOnUser
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Destinations.MAP_ROUTE,
                navigateToMap = { },
                navigateToGroups = navController::navigateToGroups,
                navigateToSocial = navController::navigateToFriends,
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            GoogleMap(
                cameraPositionState = uiState.cameraPosition,
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    zoomControlsEnabled = false
                ),
                properties = MapProperties(
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, mapStyleId)
                ),
            ) {
                MarkerComposable(state = MarkerState(position = uiState.userMarker)) {
                    Marker(stringResource(id = R.string.map_you))
                }

                // BUG: When navigating to profile screen full name and username changes!
                uiState.friendsMarkers.forEach { entry ->
                    MarkerComposable(
                        state = MarkerState(position = entry.value),
                        onClick = {
                            navController.navigateToProfile(entry.key.uid)
                            false
                        }
                    ) {
                        Marker(
                            text = entry.key.firstName,
                            timestamp = entry.key.location.timestamp
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = MaterialTheme.spacing.medium)
            ) {
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
                    trailingIcons = listOf(
                        TextFieldIcon(navController::navigateToNotifications) {
                            IconWithNotification(
                                icon = Icons.Outlined.Notifications,
                                notificationsCount = uiState.numberOfNotifications,
                                notificationsForegroundColor = MaterialTheme.colorScheme.onPrimary,
                                notificationsBackgroundColor = MaterialTheme.colorScheme.primary,
                                modifier = it,
                            )
                        },
                        TextFieldIcon(
                            onClick = {
                                Firebase.auth.uid?.let {
                                    navController.navigateToProfile(it)
                                }
                            }
                        ) { modifier ->
                            val drawableId =
                                if (isSystemInDarkTheme()) R.drawable.profile_picture_placeholder_dark
                                else R.drawable.profile_picture_placeholder_light

                            FrescoImage(
                                imageUrl = uiState.profilePictureUri.toString(),
                                failure = {
                                    Image(
                                        painter = painterResource(id = drawableId),
                                        contentDescription = null
                                    )
                                },
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                            )
                        }
                    ),
                    placeholder = stringResource(id = R.string.users_search_placeholder),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.medium)
                        .onFocusChanged {
                            if (it.hasFocus) navController.navigateToSearch()
                        }
                )
                Chips(
                    chips = uiState.groups,
                    isAllSelected = uiState.isAllSelected,
                    toggleGroup = viewModel::toggleGroup,
                    toggleAllGroups = viewModel::toggleAllGroups,
                    navController = navController,
                )
            }
        }
    }
}