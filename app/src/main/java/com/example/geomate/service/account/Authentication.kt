package com.example.geomate.service.account

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface Authentication {
    val auth: FirebaseAuth
    val user: FirebaseUser?
    suspend fun signIn(): FirebaseUser?
    suspend fun signUp(): FirebaseUser?
}
