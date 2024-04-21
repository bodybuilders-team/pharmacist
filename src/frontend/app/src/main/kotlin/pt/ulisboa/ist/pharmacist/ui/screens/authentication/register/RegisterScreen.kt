package pt.ulisboa.ist.pharmacist.ui.screens.authentication.register

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
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.components.RegisterButton
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.components.RegisterTextFields
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validateEmail
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validatePassword
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validateUsername
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.ScreenTitle

/**
 * Register screen.
 *
 * @param state Authentication state
 * @param onRegister callback to be invoked when the register button is clicked
 * @param onRegisterSuccessful callback to be invoked when the register process is successful
 * @param onBackButtonClicked callback to be invoked when the back button is clicked
 */
@Composable
fun RegisterScreen(
    state: AuthenticationState,
    onRegister: (String, String, String) -> Unit,
    onRegisterSuccessful: () -> Unit,
    onBackButtonClicked: () -> Unit
) {
    LaunchedEffect(state) {
        if (state == SUCCESS)
            onRegisterSuccessful()
    }

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val invalidFields = (email.isEmpty() || username.isEmpty() || password.isEmpty()) ||
            email.isNotEmpty() && !validateEmail(email) ||
            username.isNotEmpty() && !validateUsername(username) ||
            password.isNotEmpty() && !validatePassword(password)

    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenTitle(title = stringResource(R.string.register_title))

            RegisterTextFields(
                email = email,
                username = username,
                password = password,
                onEmailChangeCallback = { email = it },
                onUsernameChangeCallback = { username = it },
                onPasswordChangeCallback = { password = it }
            )

            RegisterButton(enabled = state != AuthenticationState.LOADING) {
                if (invalidFields)
                    return@RegisterButton

                onRegister(email, username, password)
            }
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    RegisterScreen(
        state = AuthenticationState.IDLE,
        onRegister = { _, _, _ -> },
        onRegisterSuccessful = { },
        onBackButtonClicked = { }
    )
}
