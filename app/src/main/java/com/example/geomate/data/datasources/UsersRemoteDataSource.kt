package com.example.geomate.data.datasources

import android.net.Uri
import android.util.Log
import com.example.geomate.data.models.Location
import com.example.geomate.data.models.User
import com.example.geomate.ext.snapshotFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UsersRemoteDataSource(
    private val fireAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val fireBucket: FirebaseStorage,
) : UsersDataSource {
    override suspend fun getSingleAsFlow(userId: String): Flow<User?> = fireStore
        .collection("users")
        .whereEqualTo("uid", userId)
        .snapshotFlow()
        .map {
            val objects = it.toObjects(User::class.java)
            Log.d("asdqwe", "getSingleAsFlow: $objects")
                objects.firstOrNull() }

    override suspend fun getAllAsFlow(usersIds: List<String>): Flow<List<User>> = fireStore
        .collection("users")
        .whereIn("uid", usersIds)
        .snapshotFlow()
        .map { it.toObjects(User::class.java) }

    override suspend fun getSingle(userId: String): User? = fireStore
        .collection("users")
        .whereEqualTo("uid", userId)
        .get().await().toObjects(User::class.java).firstOrNull()

    override suspend fun getAll(usersIds: List<String>): List<User> {
        if (usersIds.isEmpty()) {
            return emptyList()
        }
        return fireStore.collection("users")
            .whereIn("uid", usersIds)
            .get().await().toObjects(User::class.java)
    }

    override suspend fun matchFirstName(searchQuery: String): List<User> = fireStore
        .collection("users")
        .whereGreaterThanOrEqualTo("firstName", searchQuery)
        .whereLessThan("firstName", searchQuery + '\uf8ff')
        .get().await().toObjects(User::class.java)

    override suspend fun matchLastName(searchQuery: String): List<User> = fireStore
        .collection("users")
        .whereGreaterThanOrEqualTo("lastName", searchQuery)
        .whereLessThan("lastName", searchQuery + '\uf8ff')
        .get().await().toObjects(User::class.java)

    override suspend fun matchUsername(searchQuery: String): List<User> = fireStore
        .collection("users")
        .whereGreaterThanOrEqualTo("username", searchQuery)
        .whereLessThan("username", searchQuery + '\uf8ff')
        .get().await().toObjects(User::class.java)

    override suspend fun add(user: User) {
        fireStore.collection("users").add(user).await()
    }

    override suspend fun update(userId: String, updates: Map<String, Any>) {
        fireStore.collection("users")
            .whereEqualTo("uid", userId)
            .get().await()
            .documents.first().reference.update(updates)
    }


    override suspend fun remove(user: User) {
        fireStore.collection("users")
            .whereEqualTo("uid", user.uid)
            .get().await()
            .documents.first().reference.delete()
    }

    override suspend fun getProfilePicture(userId: String): Uri {
        val reference = fireBucket.reference.child("profile-pictures/$userId")
        return try {
            reference.downloadUrl.await()
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }

    override suspend fun addProfilePicture(userId: String, uri: Uri) {
        val reference = fireBucket.reference.child("profile-pictures/$userId")
        reference.putFile(uri)
    }

    override suspend fun sendRecoveryEmail(email: String) {
        fireAuth.sendPasswordResetEmail(email).await()
    }

    override suspend fun updateLocation(location: Location) {
        fireStore.collection("users")
            .whereEqualTo("uid", fireAuth.uid)
            .get().await()
            .documents.first().reference
            .update("location", location)
    }
}