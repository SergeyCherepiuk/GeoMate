package com.example.geomate.data.datasources

import android.net.Uri
import com.example.geomate.data.models.Location
import com.example.geomate.data.models.User
import kotlinx.coroutines.flow.Flow

interface UsersDataSource {
    suspend fun getSingleAsFlow(userId: String): Flow<User?>
    suspend fun getAllAsFlow(usersIds: List<String>): Flow<List<User>>

    suspend fun getSingle(userId: String): User?
    suspend fun getAll(usersIds: List<String>): List<User>
    suspend fun matchFirstName(searchQuery: String): List<User>
    suspend fun matchLastName(searchQuery: String): List<User>
    suspend fun matchUsername(searchQuery: String): List<User>
    suspend fun add(user: User)
    suspend fun update(userId: String, updates: Map<String, Any>)
    suspend fun remove(user: User)
    suspend fun getProfilePicture(userId: String): Uri
    suspend fun addProfilePicture(userId: String, uri: Uri)
    suspend fun sendRecoveryEmail(email: String)

    suspend fun updateLocation(location: Location)
}