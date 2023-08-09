package com.example.geomate.ui.screens.signup

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.geomate.R
import com.example.geomate.ext.isEmailValid
import com.example.geomate.ext.isFirstNameValid
import com.example.geomate.ext.isLastNameValid
import com.example.geomate.ext.isPasswordValid
import com.example.geomate.ext.isUsernameValid
import com.example.geomate.ui.components.ButtonType
import com.example.geomate.ui.components.Footer
import com.example.geomate.ui.components.GeoMateButton
import com.example.geomate.ui.components.GeoMateTextField
import com.example.geomate.ui.components.Header
import com.example.geomate.ui.components.InputValidator
import com.example.geomate.ui.components.LeadingIcon
import com.example.geomate.ui.components.ProfilePicturePicker
import com.example.geomate.ui.components.SocialNetworksRow
import com.example.geomate.ui.components.TrailingIcon
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.screens.signin.navigateToSignIn
import com.example.geomate.ui.theme.GeoMateTheme
import com.example.geomate.ui.theme.spacing
import kotlinx.coroutines.launch

fun NavGraphBuilder.signUp(
    navController: NavController,
    viewModel: SignUpViewModel,
) {
    composable(Destinations.SIGN_UP_ROUTE) {
        val uiState by viewModel.uiState.collectAsState()
        SignUpScreen(
            uiState = uiState,
            updateEmail = viewModel::updateEmail,
            updatePassword = viewModel::updatePassword,
            updateFirstName = viewModel::updateFirstName,
            updateLastName = viewModel::updateLastName,
            updateUsername = viewModel::updateUsername,
            updateProfilePictureUri = viewModel::updateProfilePictureUri,
            updateBio = viewModel::updateBio,
            onSignUpClick = viewModel::onSignUpClick,
            onFacebookClick = { /* TODO: Sign in with facebook */ },
            onGoogleClick = { /* TODO: Sign in with google */ },
            onTwitterClick = { /* TODO: Sign in with twitter */ },
            navigateToSignIn = navController::navigateToSignIn,
            checkIfUsernameTaken = viewModel::isUsernameTaken
        )
    }
}

fun NavController.navigateToSignUp() {
    popBackStack()
    navigate(Destinations.SIGN_UP_ROUTE) {
        launchSingleTop = false
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    updateFirstName: (String) -> Unit,
    updateLastName: (String) -> Unit,
    updateUsername: (String) -> Unit,
    updateProfilePictureUri: (Uri?) -> Unit,
    updateBio: (String) -> Unit,
    checkIfUsernameTaken: (String) -> Boolean,
    onSignUpClick: () -> Boolean,
    onFacebookClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onTwitterClick: () -> Unit,
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isFirstNameValid by remember { mutableStateOf(true) }
    var isLastNameValid by remember { mutableStateOf(true) }
    var isUsernameValid by remember { mutableStateOf(true) }
    var isUsernameTaken by remember { mutableStateOf(false) }

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
        val coroutineScope = rememberCoroutineScope()
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) {
            when (it) {
                0 -> EmailAndPasswordStage(
                    email = uiState.email,
                    updateEmail = updateEmail,
                    isEmailValid = isEmailValid,
                    updateIsEmailValid = { isEmailValid = it },
                    password = uiState.password,
                    updatePassword = updatePassword,
                    isPasswordValid = isPasswordValid,
                    updateIsPasswordValid = { isPasswordValid = it },
                    next = {
                        isEmailValid = uiState.email.isEmailValid()
                        isPasswordValid = uiState.password.isPasswordValid()
                        if (isEmailValid && isPasswordValid) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    },
                    onFacebookClick = onFacebookClick,
                    onGoogleClick = onGoogleClick,
                    onTwitterClick = onTwitterClick,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )

                1 -> PublicInformationStage(
                    firstName = uiState.firstName,
                    updateFirstName = updateFirstName,
                    isFirstNameValid = isFirstNameValid,
                    updateIsFirstNameValid = { isFirstNameValid = it },
                    lastName = uiState.lastName,
                    updateLastName = updateLastName,
                    isLastNameValid = isLastNameValid,
                    updateIsLastNameValid = { isLastNameValid = it },
                    username = uiState.username,
                    updateUsername = updateUsername,
                    isUsernameValid = isUsernameValid,
                    updateIsUsernameValid = { isUsernameValid = it },
                    isUsernameTaken = isUsernameTaken,
                    checkIfUsernameTaken = checkIfUsernameTaken,
                    updateIsUsernameTaken = { isUsernameTaken = it },
                    next = {
                        isFirstNameValid = uiState.firstName.isFirstNameValid()
                        isLastNameValid = uiState.lastName.isLastNameValid()
                        isUsernameValid = uiState.username.isUsernameValid()
                        isUsernameTaken = checkIfUsernameTaken(uiState.username)

                        if (isFirstNameValid && isLastNameValid && isUsernameValid && !isUsernameTaken) {
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
                    profilePictureUri = uiState.profilePictureUri,
                    updateProfilePictureUri = updateProfilePictureUri,
                    description = uiState.bio,
                    updateBio = updateBio,
                    next = {
                        val result = onSignUpClick()
                        if (result) {
                            // TODO: Navigate to the map screen
                        } else {
                            // TODO: Display error message
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
            onClick = navigateToSignIn,
            modifier = Modifier.padding(bottom = 42.dp, start = 30.dp, end = 30.dp)
        )
    }
}

@Composable
private fun EmailAndPasswordStage(
    email: String,
    updateEmail: (String) -> Unit,
    isEmailValid: Boolean,
    updateIsEmailValid: (Boolean) -> Unit,
    password: String,
    updatePassword: (String) -> Unit,
    isPasswordValid: Boolean,
    updateIsPasswordValid: (Boolean) -> Unit,
    next: () -> Unit,
    onFacebookClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onTwitterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                value = email,
                onValueChange = updateEmail,
                leadingIcon = LeadingIcon(Icons.Outlined.Email),
                placeholder = stringResource(id = R.string.email_placeholder),
                inputValidator = InputValidator(
                    isValid = isEmailValid,
                    updateIsValid = updateIsEmailValid,
                    rule = String::isEmailValid,
                    errorMessage = stringResource(id = R.string.invalid_email)
                )
            )
            GeoMateTextField(
                value = password,
                onValueChange = updatePassword,
                leadingIcon = LeadingIcon(Icons.Outlined.Lock),
                trailingIcon = TrailingIcon(
                    icon = passwordTrailingIcon,
                    onClick = { isPasswordVisible = !isPasswordVisible }
                ),
                placeholder = stringResource(id = R.string.password_placeholder),
                inputValidator = InputValidator(
                    isValid = isPasswordValid,
                    updateIsValid = updateIsPasswordValid,
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
        SocialNetworksRow(
            onFacebookClick = onFacebookClick,
            onGoogleClick = onGoogleClick,
            onTwitterClick = onTwitterClick
        )
    }
}

@Composable
private fun PublicInformationStage(
    firstName: String,
    updateFirstName: (String) -> Unit,
    isFirstNameValid: Boolean,
    updateIsFirstNameValid: (Boolean) -> Unit,
    lastName: String,
    updateLastName: (String) -> Unit,
    isLastNameValid: Boolean,
    updateIsLastNameValid: (Boolean) -> Unit,
    username: String,
    updateUsername: (String) -> Unit,
    isUsernameValid: Boolean,
    updateIsUsernameValid: (Boolean) -> Unit,
    isUsernameTaken: Boolean,
    checkIfUsernameTaken: (String) -> Boolean,
    updateIsUsernameTaken: (Boolean) -> Unit,
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
            value = firstName,
            onValueChange = updateFirstName,
            leadingIcon = LeadingIcon(Icons.Outlined.Person),
            placeholder = stringResource(id = R.string.first_name_placeholder),
            inputValidator = InputValidator(
                isValid = isFirstNameValid,
                updateIsValid = updateIsFirstNameValid,
                rule = { it.length in 1..30 },
                errorMessage = stringResource(id = R.string.invalid_firstname_sign_up)
            )
        )
        GeoMateTextField(
            value = lastName,
            onValueChange = updateLastName,
            leadingIcon = LeadingIcon(Icons.Outlined.Person),
            placeholder = stringResource(id = R.string.last_name_placeholder),
            inputValidator = InputValidator(
                isValid = isLastNameValid,
                updateIsValid = updateIsLastNameValid,
                rule = { it.length in 1..30 },
                errorMessage = stringResource(id = R.string.invalid_lastname_sign_up)
            )
        )
        GeoMateTextField(
            value = username,
            onValueChange = updateUsername,
            leadingIcon = LeadingIcon(Icons.Outlined.Person),
            placeholder = stringResource(id = R.string.username_placeholder),
            inputValidator = InputValidator(
                isValid = isUsernameValid && !isUsernameTaken,
                updateIsValid = updateIsUsernameValid,
                rule = { !checkIfUsernameTaken(it) && it.isUsernameValid() }, // testing
                errorMessage = stringResource(id = R.string.invalid_username_taken_sign_up) //testing
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
    profilePictureUri: Uri?,
    updateProfilePictureUri: (Uri?) -> Unit,
    description: String,
    updateBio: (String) -> Unit,
    next: () -> Unit,
    prev: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        updateProfilePictureUri(uri)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier
    ) {
        val bitmap: Bitmap? = profilePictureUri?.run {
            val source = ImageDecoder.createSource(context.contentResolver, this)
            ImageDecoder.decodeBitmap(source)
        }

        ProfilePicturePicker(
            bitmap = bitmap,
            openPhotoPicker = { launcher.launch("image/*") },
            clearProfilePicture = { updateProfilePictureUri(null) }
        )
        GeoMateTextField(
            value = description,
            onValueChange = updateBio,
            leadingIcon = LeadingIcon(Icons.Outlined.PermContactCalendar),
            placeholder = stringResource(id = R.string.description_placeholder),
            supportingText = "Optional",
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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SignUpScreenPreview() {
    GeoMateTheme {
        SignUpScreen(
            uiState = SignUpUiState(),
            updateEmail = { },
            updatePassword = { },
            updateFirstName = { },
            updateLastName = { },
            updateUsername = { },
            updateProfilePictureUri = { },
            updateBio = { },
            onSignUpClick = { true },
            onFacebookClick = { /* TODO: Sign in with facebook */ },
            onGoogleClick = { /* TODO: Sign in with google */ },
            onTwitterClick = { /* TODO: Sign in with twitter */ },
            navigateToSignIn = { },
            checkIfUsernameTaken = { true }

        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFF7F0
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF2A2A2A
)
@Composable
private fun EmailAndPasswordStagePreview() {
    GeoMateTheme {
        EmailAndPasswordStage(
            email = "",
            updateEmail = { },
            isEmailValid = true,
            updateIsEmailValid = { },
            password = "",
            updatePassword = { },
            isPasswordValid = true,
            updateIsPasswordValid = { },
            next = { },
            onFacebookClick = { /* TODO: Sign in with facebook */ },
            onGoogleClick = { /* TODO: Sign in with google */ },
            onTwitterClick = { /* TODO: Sign in with twitter */ },
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFF7F0
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF2A2A2A
)
@Composable
private fun PublicInformationStagePreview() {
    GeoMateTheme {
        PublicInformationStage(
            firstName = "",
            updateFirstName = { },
            isFirstNameValid = true,
            updateIsFirstNameValid = { },
            lastName = "",
            updateLastName = { },
            isLastNameValid = true,
            updateIsLastNameValid = { },
            username = "",
            updateUsername = { },
            isUsernameValid = true,
            updateIsUsernameValid = { },
            next = { },
            prev = { },
            isUsernameTaken = false,
            updateIsUsernameTaken = { },
            checkIfUsernameTaken = { false }
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFF7F0
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF2A2A2A
)
@Composable
private fun OptionalInformationStagePreview() {
    GeoMateTheme {
        OptionalInformationStage(
            profilePictureUri = Uri.EMPTY,
            updateProfilePictureUri = { },
            description = "",
            updateBio = { },
            next = { },
            prev = { }
        )
    }
}