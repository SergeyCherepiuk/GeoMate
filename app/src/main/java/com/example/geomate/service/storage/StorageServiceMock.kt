package com.example.geomate.service.storage

import com.example.geomate.model.User

class StorageServiceMock : StorageService {
    override suspend fun addUser(user: User) { TODO() }
    override suspend fun getUser(uid: String): User? { TODO() }
}