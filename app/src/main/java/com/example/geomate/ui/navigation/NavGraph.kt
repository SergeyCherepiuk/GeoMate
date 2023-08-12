package com.example.geomate.ui.navigation

import android.app.Application
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.geomate.service.account.FirebaseAccountService
import com.example.geomate.service.storage.FirebaseStorageService
import com.example.geomate.ui.screens.forgotpassword.ForgotPasswordViewModelImpl
import com.example.geomate.ui.screens.forgotpassword.forgotPassword
import com.example.geomate.ui.screens.map.MapViewModelImpl
import com.example.geomate.ui.screens.map.map
import com.example.geomate.ui.screens.signin.SignInViewModelImpl
import com.example.geomate.ui.screens.signin.signIn
import com.example.geomate.ui.screens.signup.SignUpViewModelImpl
import com.example.geomate.ui.screens.signup.signUp
import com.example.geomate.ui.theme.GeoMateTheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NavGraph(application: Application, navController: NavHostController) {
    val storageService = FirebaseStorageService(FirebaseFirestore.getInstance())
    val accountService = FirebaseAccountService(
        FirebaseAuth.getInstance(),
        storageService
    )
    val signInViewModel = SignInViewModelImpl(storageService)
    val signUpViewModel = SignUpViewModelImpl(storageService)
    val forgotPasswordViewModel = ForgotPasswordViewModelImpl(accountService)
    val mapViewModel = MapViewModelImpl(
        application,
        LocationServices.getFusedLocationProviderClient(LocalContext.current)
    )

    GeoMateTheme {
        NavHost(
            navController = navController,
            startDestination = Destinations.SIGN_IN_ROUTE,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(0, easing = LinearEasing),
                    initialAlpha = 1f
                )
            },
            exitTransition = {
                fadeOut(tween(0, easing = LinearEasing))
            }
        ) {
            forgotPassword(
                viewModel = forgotPasswordViewModel,
                navController = navController
            )
            signIn(
                viewModel = signInViewModel,
                navController = navController
            )
            signUp(
                viewModel = signUpViewModel,
                navController = navController
            )
            map(
                viewModel = mapViewModel,
                navController = navController
            )
        }
    }
}