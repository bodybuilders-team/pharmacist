package pt.ulisboa.ist.pharmacist.ui.screens.authentication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.components.PasswordTextField
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.components.UsernameTextField
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.ScreenTitle

private const val BUTTON_PADDING = 8
private const val USERNAME_TO_PASSWORD_PADDING = 8
private const val TEXT_FIELD_WIDTH_FACTOR = 0.6f

/**
 * Authentication screen.
 *
 * @param state the authentication state
 * @param onAuthenticate callback to be invoked when the authentication button is clicked
 * @param authenticationMethod the authentication method
 */
@Composable
fun AuthenticationScreen(
    state: AuthenticationViewModel.AuthenticationState,
    authenticationMethod: AuthenticationActivity.Companion.AuthenticationMethod,
    onAuthenticate: (String, String) -> Unit
) {
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
            ScreenTitle(
                title = when (authenticationMethod) {
                    AuthenticationActivity.Companion.AuthenticationMethod.LOGIN -> "Login"
                    AuthenticationActivity.Companion.AuthenticationMethod.UPGRADE -> "Upgrade"
                    AuthenticationActivity.Companion.AuthenticationMethod.REGISTER -> "Register"
                }
            )

            Column(modifier = Modifier.fillMaxWidth(TEXT_FIELD_WIDTH_FACTOR)) {
                UsernameTextField(
                    username = username,
                    onUsernameChangeCallback = { username = it },
                    modifier = Modifier.padding(bottom = USERNAME_TO_PASSWORD_PADDING.dp)
                )
                PasswordTextField(password = password, onPasswordChangeCallback = { password = it })
            }

            IconTextButton(
                onClick = {
                    if (invalidFields)
                        return@IconTextButton

                    onAuthenticate(username, password)
                },
                enabled = state != AuthenticationViewModel.AuthenticationState.AUTHENTICATED,
                imageVector = when (authenticationMethod) {
                    AuthenticationActivity.Companion.AuthenticationMethod.LOGIN -> Icons.AutoMirrored.Rounded.Login
                    AuthenticationActivity.Companion.AuthenticationMethod.UPGRADE -> Icons.Rounded.Upgrade
                    AuthenticationActivity.Companion.AuthenticationMethod.REGISTER -> Icons.Rounded.PersonAdd
                },
                contentDescription = when (authenticationMethod) {
                    AuthenticationActivity.Companion.AuthenticationMethod.LOGIN -> "Login"
                    AuthenticationActivity.Companion.AuthenticationMethod.UPGRADE -> "Upgrade"
                    AuthenticationActivity.Companion.AuthenticationMethod.REGISTER -> "Register"
                },
                text = when (authenticationMethod) {
                    AuthenticationActivity.Companion.AuthenticationMethod.LOGIN -> "Login"
                    AuthenticationActivity.Companion.AuthenticationMethod.UPGRADE -> "Upgrade"
                    AuthenticationActivity.Companion.AuthenticationMethod.REGISTER -> "Register"
                },
                modifier = Modifier.padding(BUTTON_PADDING.dp),
            )
        }
    }
}

