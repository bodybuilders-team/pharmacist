package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login

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
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.upgrade.UpgradeViewModel.UpgradeState
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.upgrade.components.UpgradeButton
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.upgrade.components.UpgradeTextFields
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validatePassword
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validateUsername
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.ScreenTitle

/**
 * Upgrade screen.
 *
 * @param state the authentication state
 * @param onUpgrade callback to be invoked when the upgrade button is clicked
 */
@Composable
fun UpgradeScreen(
    state: UpgradeState,
    onUpgrade: (String, String) -> Unit
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
            ScreenTitle(title = "Upgrade Account")

            UpgradeTextFields(
                username = username,
                password = password,
                onUsernameChangeCallback = { username = it },
                onPasswordChangeCallback = { password = it }
            )

            UpgradeButton(enabled = state != UpgradeState.LOGGING_IN) {
                if (invalidFields)
                    return@UpgradeButton

                onUpgrade(username, password)
            }
        }
    }
}

