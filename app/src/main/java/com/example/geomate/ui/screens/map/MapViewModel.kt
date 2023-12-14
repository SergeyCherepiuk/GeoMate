package com.example.geomate.ui.screens.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geomate.data.models.Group
import com.example.geomate.data.models.Location
import com.example.geomate.data.repositories.FriendshipRepository
import com.example.geomate.data.repositories.GroupsRepository
import com.example.geomate.data.repositories.NotificationRepository
import com.example.geomate.data.repositories.UsersRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    application: Application,
    private val groupsRepository: GroupsRepository,
    private val usersRepository: UsersRepository,
    private val friendshipRepository: FriendshipRepository,
    private val notificationRepository: NotificationRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
) : AndroidViewModel(application) {
    private var locationFetchingJob: Job? = null
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun fetchGroups(userId: String) {
        viewModelScope.launch {
            groupsRepository.getAllAsFlow(userId).collect { groups ->
                _uiState.update { it.copy(groups = groups.associateWith { true }.toMutableMap()) }
            }
        }
    }

    fun startFetchingFriendsLocation() {
        locationFetchingJob = viewModelScope.launch {
            while (true) {
                val friends = friendshipRepository.getFriends()
                _uiState.update {
                    it.copy(
                        friendsMarkers = friends.associateWith { friend ->
                            LatLng(friend.location.latitude, friend.location.longitude)
                        }
                    )
                }
                delay(5000L)
            }
        }
    }

    fun stopFetchingFriendsLocation() {
        locationFetchingJob?.cancel()
        locationFetchingJob = null
    }

    fun fetchNumberOfNotifications() {
        viewModelScope.launch {
            notificationRepository.getUserSentAsFlow().collect { notifications ->
                _uiState.update {
                    it.copy(numberOfNotifications = notifications.size)
                }
            }
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        _uiState.update { it.copy(searchQuery = searchQuery) }
    }

    suspend fun fetchProfilePicture(userId: String) {
        viewModelScope.launch {
            val uri = usersRepository.getProfilePicture(userId)
            _uiState.update { it.copy(profilePictureUri = uri) }
        }
    }

    fun startMonitoringUserLocation() {
        val request = LocationRequest.Builder(10L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    val userLocation = Location(location.latitude, location.longitude)
                    // TODO: Launch separate a coroutine to read latest location from UiState every 5 seconds,
                    //  because Android might send updated location more frequently (e.g. every seconds).
                    viewModelScope.launch {
                        usersRepository.updateLocation(userLocation)
                    }
                    _uiState.update {
                        it.copy(userMarker = LatLng(location.latitude, location.longitude))
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

    fun stopMonitoringUserLocation() {
        // TODO: Implement (currently not needed)
    }

    fun toggleGroup(group: Group) {
        val groups = uiState.value.groups.toMutableMap()
        groups[group] = groups[group]?.not() ?: false
        _uiState.update {
            it.copy(groups = groups)
        }
    }

    fun toggleAllGroups(current: Boolean) {
        val groups = uiState.value.groups.toMutableMap()
        groups.replaceAll { _, _ -> !current }
        _uiState.update {
            it.copy(groups = groups)
        }
    }

    fun pointCameraOnUser() {
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
}