package com.example.geomate.data.repositories

import android.net.Uri
import com.example.geomate.data.datasources.UsersDataSource
import com.example.geomate.data.models.Location
import com.example.geomate.data.models.User
import kotlinx.coroutines.flow.Flow

class UsersRepository(private val usersDataSource: UsersDataSource) {
    private val allUsersFlows: MutableMap<List<String>, Flow<List<User>>> = mutableMapOf()
    private val profilePictures: MutableMap<String, Uri> = mutableMapOf()

    suspend fun getAllAsFlow(usersIds: List<String>): Flow<List<User>> {
        return allUsersFlows[usersIds] ?: run {
            val users = usersDataSource.getAllAsFlow(usersIds)
            allUsersFlows[usersIds] = users
            return users
        }
    }

    suspend fun getSingleAsFlow(userId: String): Flow<User?> = usersDataSource.getSingleAsFlow(userId)

    suspend fun getSingle(userId: String): User? = usersDataSource.getSingle(userId)

    suspend fun matchFirstName(searchQuery: String): List<User> =
        usersDataSource.matchFirstName(searchQuery)

    suspend fun matchLastName(searchQuery: String): List<User> =
        usersDataSource.matchLastName(searchQuery)

    suspend fun matchUsername(searchQuery: String): List<User> =
        usersDataSource.matchUsername(searchQuery)

    suspend fun add(user: User) = usersDataSource.add(user)

    suspend fun update(userId: String, updates: Map<String, Any>) =
        usersDataSource.update(userId, updates)

    suspend fun remove(user: User) = usersDataSource.remove(user)

    suspend fun getProfilePicture(userId: String): Uri {
        return profilePictures[userId] ?: run {
            val profilePicture = usersDataSource.getProfilePicture(userId)
            profilePictures[userId] = profilePicture
            return profilePicture
        }
    }

    suspend fun addProfilePicture(userId: String, uri: Uri) {
        profilePictures[userId] = uri
        usersDataSource.addProfilePicture(userId, uri)
    }

    suspend fun sendRecoveryEmail(email: String) = usersDataSource.sendRecoveryEmail(email)

    suspend fun updateLocation(location: Location) = usersDataSource.updateLocation(location)
}