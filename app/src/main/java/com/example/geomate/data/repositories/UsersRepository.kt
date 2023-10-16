package com.example.geomate.data.repositories

import android.net.Uri
import com.example.geomate.data.datasources.UsersDataSource
import com.example.geomate.data.models.User
import kotlinx.coroutines.flow.Flow

class UsersRepository(private val usersDataSource: UsersDataSource) {
    private val userFlows: MutableMap<String, Flow<User?>> = mutableMapOf()
    private val allUsersFlows: MutableMap<List<String>, Flow<List<User>>> = mutableMapOf()
    private val profilePictures: MutableMap<String, Uri> = mutableMapOf()

    suspend fun get(userId: String): Flow<User?> {
        return userFlows[userId] ?: run {
            val user = usersDataSource.get(userId)
            userFlows[userId] = user
            return user
        }
    }

    suspend fun getAll(usersIds: List<String>): Flow<List<User>> {
        return allUsersFlows[usersIds] ?: run {
            val users = usersDataSource.getAll(usersIds)
            allUsersFlows[usersIds] = users
            return users
        }
    }

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
    suspend fun sendRecoveryPassword(email: String) = usersDataSource.sendRecoveryPassword(email)
}