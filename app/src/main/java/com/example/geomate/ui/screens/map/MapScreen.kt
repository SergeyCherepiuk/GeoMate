package com.example.geomate.ui.screens.map

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Picture
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.geomate.R
import com.example.geomate.ext.toBitmap
import com.example.geomate.ext.toPicture
import com.example.geomate.image.BitmapComposable
import com.example.geomate.image.toBitmap
import com.example.geomate.image.toBitmapWithText
import com.example.geomate.ui.components.GeoMateFAB
import com.example.geomate.ui.components.GeoMateTextField
import com.example.geomate.ui.components.IconWithNotification
import com.example.geomate.ui.components.TextFieldIcon
import com.example.geomate.ui.components.bottomnavbar.BottomNavigationBar
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.screens.friends.navigateToFriends
import com.example.geomate.ui.screens.groups.navigateToGroups
import com.example.geomate.ui.screens.map.components.Chips
import com.example.geomate.ui.screens.map.components.MapMarker
import com.example.geomate.ui.screens.notifications.navigateToNotifications
import com.example.geomate.ui.screens.profile.navigateToProfile
import com.example.geomate.ui.screens.search.navigateToSearch
import com.example.geomate.ui.theme.GeoMateTheme
import com.example.geomate.ui.theme.spacing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
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
//        viewModel.fetchFriends()
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
                // TODO: Refactor this crap
//                val userMarkerId =
//                    if (isSystemInDarkTheme()) R.drawable.you_marker_dark
//                    else R.drawable.you_marker_light
//                val userMarker = context.resources.getDrawable(userMarkerId, null)

//                val markerView = ComposeView(context).apply {
//                    disposeComposition()
//                    setContent {
//                        GeoMateTheme {
//                            MapMarker(fullName = "You") {
//                                Image(imageVector = Icons.Default.GpsFixed, contentDescription = null)
//                            }
//                        }
//                    }
//                }.drawToBitmap()

//                var bitmap: Bitmap? = null
//                BitmapComposable(
//                    onBitmapped = { b -> bitmap = b },
//                    intSize = IntSize(500, 700) // Pixel size for output bitmap
//                ) {
//                    // Composable that you want to convert to a bitmap
//                    // This scope is @Composable
//                            MapMarker(fullName = "You") {
//                                Image(imageVector = Icons.Default.GpsFixed, contentDescription = null)
//                            }
//                }

                val marker = LayoutInflater.from(context).inflate(R.layout.marker, null)
                marker.layout(0, 0, 100, 100)

                Marker(
                    state = MarkerState(position = uiState.userMarker),
                    icon = BitmapDescriptorFactory.fromBitmap(marker.drawToBitmap())
                )

//                val friendMarkerId =
//                    if (isSystemInDarkTheme()) R.drawable.friend_marker_dark
//                    else R.drawable.friend_marker_light
//                val friendMarker = context.resources.getDrawable(friendMarkerId, null)
//
//                uiState.friendsMarkers.forEach { entry ->
//                    Marker(
//                        state = MarkerState(position = entry.value),
//                        icon = BitmapDescriptorFactory.fromBitmap(markerView)
//                    )
//                }
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