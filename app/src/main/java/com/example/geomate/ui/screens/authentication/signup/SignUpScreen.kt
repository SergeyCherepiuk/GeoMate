package com.example.geomate.ui.screens.authentication.signup

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PermContactCalendar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.geomate.R
import com.example.geomate.authentication.EmailAndPasswordSignUp
import com.example.geomate.authentication.FacebookAuthentication
import com.example.geomate.authentication.GoogleAuthentication
import com.example.geomate.authentication.TwitterAuthentication
import com.example.geomate.ext.isEmailValid
import com.example.geomate.ext.isFirstNameValid
import com.example.geomate.ext.isLastNameValid
import com.example.geomate.ext.isPasswordValid
import com.example.geomate.ext.isUsernameValid
import com.example.geomate.ui.components.ButtonType
import com.example.geomate.ui.components.GeoMateButton
import com.example.geomate.ui.components.GeoMateTextField
import com.example.geomate.ui.components.InputValidator
import com.example.geomate.ui.components.ProfilePicturePicker
import com.example.geomate.ui.components.TextFieldIcon
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.screens.authentication.components.Footer
import com.example.geomate.ui.screens.authentication.components.Header
import com.example.geomate.ui.screens.authentication.components.SocialNetworks
import com.example.geomate.ui.screens.authentication.signin.navigateToSignIn
import com.example.geomate.ui.screens.map.navigateToMap
import com.example.geomate.ui.theme.spacing
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

fun NavGraphBuilder.signUp(
    viewModel: SignUpViewModel,
    navController: NavController,
) {
    composable(Destinations.SIGN_UP_ROUTE) {
        val uiState by viewModel.uiState.collectAsState()
        SignUpScreen(
            uiState = uiState,
            viewModel = viewModel,
            navController = navController
        )
    }
}

fun NavController.navigateToSignUp() {
    popBackStack()
    navigate(Destinations.SIGN_UP_ROUTE) {
        launchSingleTop = true
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    viewModel: SignUpViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Header(
            title = stringResource(id = R.string.sign_up_title),
            subtitle = stringResource(id = R.string.sign_up_subtitle),
            modifier = Modifier.padding(top = 42.dp, start = 30.dp, end = 30.dp)
        )

        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { 3 }
        )
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) {
            when (it) {
                0 -> EmailAndPasswordStage(
                    uiState = uiState,
                    viewModel = viewModel,
                    navController = navController,
                    next = {
                        val isEmailValid = uiState.email.isEmailValid()
                        viewModel.updateIsEmailValid(isEmailValid)

                        val isPasswordValid = uiState.password.isPasswordValid()
                        viewModel.updateIsPasswordValid(isEmailValid)

                        if (isEmailValid && isPasswordValid) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 30.dp)
                )

                1 -> PublicInformationStage(
                    uiState = uiState,
                    viewModel = viewModel,
                    next = {
                        val isFirstNameValid = uiState.firstName.isFirstNameValid()
                        viewModel.updateIsFirstNameValid(isFirstNameValid)

                        val isLastNameValid = uiState.lastName.isLastNameValid()
                        viewModel.updateIsLastNameValid(isLastNameValid)

                        val isUsernameValid = uiState.username.isUsernameValid()
                        viewModel.updateIsUsernameValid(isUsernameValid)

                        if (isFirstNameValid && isLastNameValid && isUsernameValid) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        }
                    },
                    prev = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 30.dp)
                )

                2 -> OptionalInformationStage(
                    uiState = uiState,
                    viewModel = viewModel,
                    next = {
                        coroutineScope.launch {
                            val user = viewModel.signUp(
                                EmailAndPasswordSignUp(
                                    email = uiState.email,
                                    password = uiState.password,
                                    username = uiState.username,
                                    firstName = uiState.firstName,
                                    lastName = uiState.lastName,
                                    bio = uiState.bio,
                                    uri = uiState.profilePictureUri,
                                    usersRepository = viewModel.usersRepository
                                )
                            )
                            if (user != null) {
                                navController.navigateToMap()
                            } else {
                                Toast(context).apply {
                                    setText("Authentication failed!")
                                    duration = Toast.LENGTH_SHORT
                                }.show()
                            }
                        }
                    },
                    prev = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
            }
        }

        Footer(
            text = stringResource(id = R.string.sign_up_footer),
            clickableText = stringResource(id = R.string.button_sign_in),
            onClick = navController::navigateToSignIn,
            modifier = Modifier.padding(bottom = 42.dp, start = 30.dp, end = 30.dp)
        )
    }
}

@Composable
private fun EmailAndPasswordStage(
    uiState: SignUpUiState,
    viewModel: SignUpViewModel,
    navController: NavController,
    next: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val oneTapClient = Identity.getSignInClient(context)
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            val signInCredentials = oneTapClient.getSignInCredentialFromIntent(result.data)
            val googleSignInAuth = GoogleAuthentication(viewModel.usersRepository, signInCredentials)
            coroutineScope.launch {
                // TODO: Refactor this part (repeating down below)
                val user = viewModel.signUp(googleSignInAuth)
                if (user != null) {
                    navController.navigateToMap()
                } else {
                    Toast(context).apply {
                        setText("Authentication failed!")
                        duration = Toast.LENGTH_SHORT
                    }.show()
                }
            }
        }
    }
    val loginManager = LoginManager.getInstance()
    val callbackManager = remember { CallbackManager.Factory.create() }
    val facebookLauncher =
        rememberLauncherForActivityResult(
            contract = loginManager.createLogInActivityResultContract(
                callbackManager,
                null
            ), onResult = { activityResult ->
                if (activityResult.resultCode == ComponentActivity.RESULT_OK) {
                    val signInToken = FacebookAuthentication.getTokenFromIntent(activityResult.data)
                    val facebookSignInAuth = FacebookAuthentication(viewModel.usersRepository, signInToken)
                    coroutineScope.launch {
                        val user = viewModel.signUp(facebookSignInAuth)
                        if (user != null) {
                            navController.navigateToMap()
                        } else {
                            Toast(context).apply {
                                setText("Authentication failed!")
                                duration = Toast.LENGTH_SHORT
                            }.show()
                        }
                    }
                }
            })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            var isPasswordVisible by remember { mutableStateOf(false) }
            val (passwordTrailingIcon, passwordVisualTransformation) = when (isPasswordVisible) {
                true -> Pair(Icons.Outlined.VisibilityOff, VisualTransformation.None)
                false -> Pair(Icons.Outlined.Visibility, PasswordVisualTransformation())
            }

            GeoMateTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                leadingIcons = listOf(
                    TextFieldIcon {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = it
                        )
                    }
                ),
                placeholder = stringResource(id = R.string.email_placeholder),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                inputValidator = InputValidator(
                    isValid = uiState.isEmailValid,
                    updateIsValid = viewModel::updateIsEmailValid,
                    rule = String::isEmailValid,
                    errorMessage = stringResource(id = R.string.invalid_email)
                )
            )
            GeoMateTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                leadingIcons = listOf(
                    TextFieldIcon {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = it
                        )
                    }
                ),
                trailingIcons = listOf(
                    TextFieldIcon(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector = passwordTrailingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = it
                        )
                    }
                ),
                placeholder = stringResource(id = R.string.password_placeholder),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                inputValidator = InputValidator(
                    isValid = uiState.isPasswordValid,
                    updateIsValid = viewModel::updateIsPasswordValid,
                    rule = String::isPasswordValid,
                    errorMessage = stringResource(id = R.string.invalid_password)
                ),
                visualTransformation = passwordVisualTransformation
            )
            GeoMateButton(
                text = stringResource(id = R.string.button_continue),
                onClick = next,
                type = ButtonType.Primary
            )
        }
        SocialNetworks(
            onFacebookClick = { facebookLauncher.launch(listOf("email", "public_profile")) },
            onGoogleClick = {
                coroutineScope.launch {
                    val signInIntentSender = GoogleAuthentication.getSignUpIntentSender(oneTapClient)
                    launcher.launch(
                        IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                    )
                }
            },
            onTwitterClick = {
                coroutineScope.launch {
                    val user = viewModel.signUp(
                        TwitterAuthentication(viewModel.usersRepository, context as Activity)
                    )
                    if (user != null) {
                        navController.navigateToMap()
                    } else {
                        Toast(context).apply {
                            setText("Authentication  failed!")
                            duration = Toast.LENGTH_SHORT
                        }.show()
                    }
                }
            }
        )
    }
}

@Composable
private fun PublicInformationStage(
    uiState: SignUpUiState,
    viewModel: SignUpViewModel,
    next: () -> Unit,
    prev: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier
    ) {
        GeoMateTextField(
            value = uiState.firstName,
            onValueChange = viewModel::updateFirstName,
            leadingIcons = listOf(
                TextFieldIcon {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = it
                    )
                }
            ),
            placeholder = stringResource(id = R.string.first_name_placeholder),
            inputValidator = InputValidator(
                isValid = uiState.isFirstNameValid,
                updateIsValid = viewModel::updateIsFirstNameValid,
                rule = { it.length in 1..30 },
                errorMessage = stringResource(id = R.string.invalid_firstname)
            )
        )
        GeoMateTextField(
            value = uiState.lastName,
            onValueChange = viewModel::updateLastName,
            leadingIcons = listOf(
                TextFieldIcon {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = it
                    )
                }
            ),
            placeholder = stringResource(id = R.string.last_name_placeholder),
            inputValidator = InputValidator(
                isValid = uiState.isLastNameValid,
                updateIsValid = viewModel::updateIsLastNameValid,
                rule = { it.length in 1..30 },
                errorMessage = stringResource(id = R.string.invalid_lastname)
            )
        )
        GeoMateTextField(
            value = uiState.username,
            onValueChange = viewModel::updateUsername,
            leadingIcons = listOf(
                TextFieldIcon {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = it
                    )
                }
            ),
            placeholder = stringResource(id = R.string.username_placeholder),
            inputValidator = InputValidator(
                isValid = uiState.isUsernameValid,
                updateIsValid = viewModel::updateIsUsernameValid,
                rule = String::isUsernameValid, // TODO: Check if username is already taken
                errorMessage = stringResource(id = R.string.invalid_username)
            )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            GeoMateButton(
                text = stringResource(id = R.string.button_back),
                onClick = prev,
                type = ButtonType.Secondary
            )
            GeoMateButton(
                text = stringResource(id = R.string.button_continue),
                onClick = next,
                type = ButtonType.Primary
            )
        }
    }
}

@Composable
private fun OptionalInformationStage(
    uiState: SignUpUiState,
    viewModel: SignUpViewModel,
    next: () -> Unit,
    prev: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateProfilePictureUri(it) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier
    ) {
        val bitmap: Bitmap? = uiState.profilePictureUri?.run {
            val source = ImageDecoder.createSource(context.contentResolver, this)
            ImageDecoder.decodeBitmap(source)
        }

        ProfilePicturePicker(
            bitmap = bitmap,
            openPhotoPicker = { launcher.launch("image/*") },
            clearProfilePicture = { viewModel.updateProfilePictureUri(null) }
        )
        GeoMateTextField(
            value = uiState.bio,
            onValueChange = viewModel::updateBio,
            leadingIcons = listOf(
                TextFieldIcon {
                    Icon(
                        imageVector = Icons.Outlined.PermContactCalendar,
                        contentDescription = null,
                        modifier = it
                    )
                }
            ),
            placeholder = stringResource(id = R.string.description_placeholder),
            supportingText = stringResource(id = R.string.optional),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            GeoMateButton(
                text = stringResource(id = R.string.button_back),
                onClick = prev,
                type = ButtonType.Secondary
            )
            GeoMateButton(
                text = stringResource(id = R.string.button_sign_up),
                onClick = next,
                type = ButtonType.Primary
            )
        }
    }
}