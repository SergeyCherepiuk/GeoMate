package com.example.geomate.ui.screens.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.geomate.model.Group
import com.example.geomate.model.User
import com.example.geomate.service.bucket.BucketService
import com.example.geomate.service.storage.StorageService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModelImpl(
    application: Application,
    private val storageService: StorageService,
    private val bucketService: BucketService,
    private val fusedLocationClient: FusedLocationProviderClient,
) : AndroidViewModel(application), MapViewModel {
    private val _uiState = MutableStateFlow(
        MapUiState(
            groups = listOf(
                Group(name = "University", isSelected = false),
                Group(name = "Family", isSelected = false),
                Group(name = "Football team", isSelected = false),
                Group(name = "Discord", isSelected = false),
                Group(name = "Work", isSelected = false),
                Group(name = "Poker club", isSelected = false),
            )
        )
    )
    override val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    override fun updateSearchQuery(searchQuery: String) {
        _uiState.update { it.copy(searchQuery = searchQuery) }
    }

    override suspend fun fetchProfilePictureUri(path: String) {
        val uri = bucketService.get(path)
        _uiState.update { it.copy(profilePictureUri = uri) }
    }

    override fun startMonitoringUserLocation() {
        val request = LocationRequest.Builder(10L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    _uiState.update {
                        it.copy(
                            userMarker = LatLng(location.latitude, location.longitude),
                        )
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
            pointCameraOnUser()
        }
    }

    override fun stopMonitoringUserLocation() {
        // TODO: Implement (currently not needed)
    }

    override fun toggleGroup(group: Group) {
        val updatedGroup = group.copy(
            isSelected = !group.isSelected
        )
        val updatedGroups = uiState.value.groups.toMutableList()
        updatedGroups[updatedGroups.indexOf(group)] = updatedGroup
        _uiState.update {
            it.copy(groups = updatedGroups)
        }
    }

    override fun toggleAllGroups(current: Boolean) {
        val updatedGroups = uiState.value.groups.map {
            it.copy(isSelected = !current)
        }
        _uiState.update {
            it.copy(groups = updatedGroups)
        }
    }

    override fun pointCameraOnUser() {
        if (ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    _uiState.update {
                        it.copy(
                            cameraPosition = CameraPositionState(
                                CameraPosition(
                                    LatLng(location.latitude, location.longitude),
                                    uiState.value.cameraPosition.position.zoom,
                                    uiState.value.cameraPosition.position.tilt,
                                    uiState.value.cameraPosition.position.bearing,
                                )
                            ),
                        )
                    }
                }
        }
    }

    override suspend fun getUser(uid: String): User? {
        return storageService.getUser(uid)
    }
}