package com.example.geomate.ui.navigation

import android.app.Application
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.geomate.data.datasources.FriendshipRequestRemoteDataSource
import com.example.geomate.data.datasources.GroupsRemoteDataSource
import com.example.geomate.data.datasources.UsersRemoteDataSource
import com.example.geomate.data.repositories.FriendshipRepository
import com.example.geomate.data.repositories.GroupsRepository
import com.example.geomate.data.repositories.NotificationRepository
import com.example.geomate.data.repositories.UsersRepository
import com.example.geomate.ui.screens.authentication.forgotpassword.ForgotPasswordViewModel
import com.example.geomate.ui.screens.authentication.forgotpassword.forgotPassword
import com.example.geomate.ui.screens.authentication.signin.SignInViewModel
import com.example.geomate.ui.screens.authentication.signin.signIn
import com.example.geomate.ui.screens.authentication.signup.SignUpViewModel
import com.example.geomate.ui.screens.authentication.signup.signUp
import com.example.geomate.ui.screens.editprofile.EditProfileViewModel
import com.example.geomate.ui.screens.editprofile.editProfile
import com.example.geomate.ui.screens.friends.FriendsViewModel
import com.example.geomate.ui.screens.friends.friendsList
import com.example.geomate.ui.screens.groupdetails.GroupDetailsViewModel
import com.example.geomate.ui.screens.groupdetails.groupDetails
import com.example.geomate.ui.screens.groups.GroupsViewModel
import com.example.geomate.ui.screens.groups.groups
import com.example.geomate.ui.screens.map.MapViewModel
import com.example.geomate.ui.screens.map.map
import com.example.geomate.ui.screens.notifications.NotificationsViewModel
import com.example.geomate.ui.screens.notifications.notifications
import com.example.geomate.ui.screens.profile.ProfileViewModel
import com.example.geomate.ui.screens.profile.profile
import com.example.geomate.ui.screens.search.SearchViewModel
import com.example.geomate.ui.screens.search.search
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
    val friendshipDataSource = FriendshipRequestRemoteDataSource(FirebaseFirestore.getInstance())

    // Repositories
    val usersRepository = UsersRepository(usersDataSource)
    val groupsRepository = GroupsRepository(groupsDataSource)
    val friendshipRepository = FriendshipRepository(friendshipDataSource, groupsDataSource,usersDataSource)
    val notificationRepository = NotificationRepository(friendshipDataSource)

    // ViewModels and UiStates
    val forgotPasswordViewModel = ForgotPasswordViewModel(usersRepository)
    val signInViewModel = SignInViewModel(usersRepository)
    val signUpViewModel = SignUpViewModel(usersRepository)
    val mapViewModel = MapViewModel(
        application,
        groupsRepository,
        usersRepository,
        friendshipRepository,
        notificationRepository,
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    )
    val notificationsViewModel =
        NotificationsViewModel(usersRepository, friendshipRepository, notificationRepository)
    val searchViewModel = SearchViewModel(usersRepository)
    val groupViewModel = GroupsViewModel(usersRepository, groupsRepository)
    val groupDetailsViewModel = GroupDetailsViewModel(usersRepository, groupsRepository, friendshipRepository)
    val friendsViewModel = FriendsViewModel(usersRepository, friendshipRepository)
    val profileViewModel = ProfileViewModel(usersRepository, friendshipRepository)
    val editProfileViewModel = EditProfileViewModel(usersRepository)

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
            notifications(notificationsViewModel, navController)
            search(searchViewModel, navController)
            groups(groupViewModel, navController)
            groupDetails(groupDetailsViewModel, navController)
            friendsList(friendsViewModel, navController)
            profile(profileViewModel, navController)
            editProfile(editProfileViewModel, navController)
        }
    }
}
