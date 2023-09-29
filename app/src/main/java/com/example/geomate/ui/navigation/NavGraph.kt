package com.example.geomate.ui.navigation

import android.app.Application
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.geomate.data.datasources.GroupsRemoteDataSource
import com.example.geomate.data.datasources.UsersRemoteDataSource
import com.example.geomate.data.models.Group
import com.example.geomate.data.repositories.GroupsRepository
import com.example.geomate.data.repositories.UsersRepository
import com.example.geomate.ui.screens.forgotpassword.ForgotPasswordViewModel
import com.example.geomate.ui.screens.forgotpassword.forgotPassword
import com.example.geomate.ui.screens.groupdetails.GroupDetailsViewModel
import com.example.geomate.ui.screens.groupdetails.groupDetails
import com.example.geomate.ui.screens.groups.GroupsViewModel
import com.example.geomate.ui.screens.groups.groups
import com.example.geomate.ui.screens.map.MapViewModel
import com.example.geomate.ui.screens.map.map
import com.example.geomate.ui.screens.signin.SignInViewModel
import com.example.geomate.ui.screens.signin.signIn
import com.example.geomate.ui.screens.signup.SignUpViewModel
import com.example.geomate.ui.screens.signup.signUp
import com.example.geomate.ui.theme.GeoMateTheme
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun NavGraph(application: Application, navController: NavHostController) {
    // Data sources
    val usersDataSource = UsersRemoteDataSource(
        FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), FirebaseStorage.getInstance()
    )
    val groupsDataSource = GroupsRemoteDataSource(FirebaseFirestore.getInstance())

    // Repositories
    val usersRepository = UsersRepository(usersDataSource)
    val groupsRepository = GroupsRepository(groupsDataSource)

    // ViewModels and UiStates
    val forgotPasswordViewModel = ForgotPasswordViewModel(usersRepository)
    val signInViewModel = SignInViewModel(usersRepository)
    val signUpViewModel = SignUpViewModel(usersRepository)
    val mapViewModel = MapViewModel(
        application,
        groupsRepository,
        usersRepository,
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    )
    val groupViewModel = GroupsViewModel(usersRepository, groupsRepository)
    val groupDetailsViewModel = GroupDetailsViewModel(usersRepository, groupsRepository)

    val startDestination = when (FirebaseAuth.getInstance().currentUser) {
        null -> Destinations.SIGN_IN_ROUTE
        else -> Destinations.MAP_ROUTE
    }
    GeoMateTheme {
        NavHost(
            navController = navController,
            startDestination = startDestination,
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
            forgotPassword(forgotPasswordViewModel, navController)
            signIn(signInViewModel, navController)
            signUp(signUpViewModel, navController)
            map(mapViewModel, navController)
            groups(groupViewModel, navController)
            groupDetails(groupDetailsViewModel, navController)
        }
    }
}