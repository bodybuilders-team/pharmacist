package pt.ulisboa.ist.pharmacist.ui.screens.authentication.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.RegisterViewModel.RegisterState
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.RegisterViewModel.RegisterState.REGISTERED
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.components.RegisterButton
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.components.RegisterTextFields
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validatePassword
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validateUsername
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.ScreenTitle

/**
 * Register screen.
 *
 * @param state Authentication state
 * @param onRegister callback to be invoked when the register button is clicked
 */
@Composable
fun RegisterScreen(
    state: RegisterState,
    onRegister: (String, String) -> Unit
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
            ScreenTitle(title = stringResource(R.string.register_title))

            RegisterTextFields(
                username = username,
                password = password,
                onUsernameChangeCallback = { username = it },
                onPasswordChangeCallback = { password = it }
            )

            RegisterButton(enabled = state != REGISTERED) {
                if (invalidFields)
                    return@RegisterButton

                onRegister(username, password)
            }
        }
    }
}

