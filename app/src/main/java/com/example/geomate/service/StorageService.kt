package com.example.geomate.service

import com.example.geomate.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StorageService(
    private val fireStore: FirebaseFirestore,
) {
    suspend fun addUser(user: User) {
        fireStore.collection("user").add(user).await()
    }

    suspend fun checkIfUsernameTaken(username: String): Boolean {
        return !fireStore.collection("user")
            .whereEqualTo("username", username).get().await().isEmpty
    }

}