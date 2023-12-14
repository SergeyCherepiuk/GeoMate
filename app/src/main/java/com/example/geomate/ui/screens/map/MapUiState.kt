package com.example.geomate.ui.screens.map

import android.net.Uri
import com.example.geomate.data.models.Group
import com.example.geomate.data.models.Location
import com.example.geomate.data.models.User
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

data class MapUiState(
    val searchQuery: String = "",
    val numberOfNotifications: Int = 0,
    val profilePictureUri: Uri? = null,
    val groups: MutableMap<Group, Boolean> = mutableMapOf(),
    val userMarker: LatLng = LatLng(40.772499265817345, -73.97661507692591),
    val friendsMarkers: Map<User, LatLng> = mapOf(),
    val cameraPosition: CameraPositionState = CameraPositionState(
        CameraPosition(userMarker, 16.0f, 0.0f, 0.0f)
    ),
)

val MapUiState.isAllSelected: Boolean get() = groups.all { it.value }
