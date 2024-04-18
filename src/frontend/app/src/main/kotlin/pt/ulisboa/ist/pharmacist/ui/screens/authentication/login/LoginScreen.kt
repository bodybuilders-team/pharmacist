package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel.AuthenticationState
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel.AuthenticationState.SUCCESS
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.components.LoginButton
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.components.LoginTextFields
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validatePassword
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validateUsername
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.GoBackButton
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.ScreenTitle

/**
 * Login screen.
 *
 * @param state the authentication state
 * @param onLogin callback to be invoked when the login button is clicked
 * @param onLoginSuccessful callback to be invoked when the login process is successful
 * @param onBackButtonClicked callback to be invoked when the back button is clicked
 */
@Composable
fun LoginScreen(
    state: AuthenticationState,
    onLogin: (String, String) -> Unit,
    onLoginSuccessful: () -> Unit,
    onBackButtonClicked: () -> Unit
) {
    LaunchedEffect(state) {
        if (state == SUCCESS)
            onLoginSuccessful()
    }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val invalidFields = (username.isEmpty() || password.isEmpty()) ||
            username.isNotEmpty() && !validateUsername(username) ||
            password.isNotEmpty() && !validatePassword(password)

    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenTitle(title = stringResource(R.string.login_title))

            LoginTextFields(
                username = username,
                password = password,
                onUsernameChangeCallback = { username = it },
                onPasswordChangeCallback = { password = it }
            )

            LoginButton(enabled = state != AuthenticationState.LOADING) {
                if (invalidFields)
                    return@LoginButton

                onLogin(username, password)
            }

            GoBackButton(onClick = onBackButtonClicked)
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        state = AuthenticationState.IDLE,
        onLogin = { _, _ -> },
        onLoginSuccessful = { },
        onBackButtonClicked = { }
    )
}
