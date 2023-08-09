package com.example.geomate.service.google

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.geomate.model.User
import com.example.geomate.service.StorageService
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuth(
    private val oneTapClient: SignInClient,
) {
    private val storageService = StorageService(FirebaseFirestore.getInstance())
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        Log.d("GoogleAuth", "Entering signIn()")

        val result = try {
            Log.d("GoogleAuth", "trying to get beginsin in ")
            oneTapClient.beginSignIn(buildSignInRequest()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent) {
        Log.d("GooglegggggggggggggggAuth", intent.data.toString())
        val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            storageService.addUser(
                User(
                    email = user?.email,
                    username = user?.displayName,
                    profilePictureUri = user?.photoUrl,
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("938799297885-6n0jvcau6itn14oshjml7gis2ca82c14.apps.googleusercontent.com")
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
