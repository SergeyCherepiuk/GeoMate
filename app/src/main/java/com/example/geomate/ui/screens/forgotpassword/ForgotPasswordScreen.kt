package com.example.geomate.ui.screens.forgotpassword

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.geomate.R
import com.example.geomate.ext.isValidEmail
import com.example.geomate.ui.components.ButtonType
import com.example.geomate.ui.components.Footer
import com.example.geomate.ui.components.GeoMateButton
import com.example.geomate.ui.components.GeoMateTextField
import com.example.geomate.ui.components.Header
import com.example.geomate.ui.components.InputValidator
import com.example.geomate.ui.components.LeadingIcon
import com.example.geomate.ui.navigation.Destinations
import com.example.geomate.ui.screens.signin.navigateToSignIn
import com.example.geomate.ui.theme.GeoMateTheme
import com.example.geomate.ui.theme.spacing

fun NavGraphBuilder.forgotPassword(navController: NavController) {
    composable(Destinations.FORGOT_PASSWORD_ROUTE) {
        ForgotPasswordScreen(navigateToSignIn = navController::navigateToSignIn)
    }
}

fun NavController.navigateToForgotPassword() {
    navigate(Destinations.FORGOT_PASSWORD_ROUTE) {
        launchSingleTop = false
    }
}

@Composable
fun ForgotPasswordScreen(
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 42.dp, horizontal = 30.dp)
    ) {
        Header(
            title = stringResource(id = R.string.forgot_password_title),
            subtitle = stringResource(id = R.string.forgot_password_subtitle)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            var email by remember { mutableStateOf("") }
            GeoMateTextField(
                value = email,
                onValueChange = { newEmail -> email = newEmail },
                leadingIcon = LeadingIcon(Icons.Outlined.Email),
                placeholder = stringResource(id = R.string.email_placeholder),
                inputValidator = InputValidator(
                    rule = String::isValidEmail,
                    errorMessage = stringResource(id = R.string.invalid_email)
                )
            )
            GeoMateButton(
                text = stringResource(id = R.string.button_reset_password),
                onClick = { /* TODO: Send email */ },
                type = ButtonType.Primary
            )
        }

        Footer(
            text = stringResource(id = R.string.forgot_password_footer),
            clickableText = stringResource(id = R.string.button_back),
            onClick = navigateToSignIn
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ForgotPasswordScreenPreview() {
    GeoMateTheme {
        ForgotPasswordScreen(
            navigateToSignIn = { }
        )
    }
}