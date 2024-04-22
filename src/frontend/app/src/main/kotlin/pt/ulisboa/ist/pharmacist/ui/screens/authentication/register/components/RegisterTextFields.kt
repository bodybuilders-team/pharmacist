package pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.components.PasswordTextField
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.components.UsernameTextField

private const val USERNAME_TO_PASSWORD_PADDING = 8
private const val TEXT_FIELD_WIDTH_FACTOR = 0.6f

/**
 * The text fields for the register operation on the register page:
 * - Username field
 * - Password field
 *
 * @param username username to show
 * @param password password to show
 * @param onUsernameChangeCallback callback to be invoked when the username text is changed
 * @param onPasswordChangeCallback callback to be invoked when the password text is changed
 */
@Composable
fun RegisterTextFields(
    username: String,
    password: String,
    onUsernameChangeCallback: (String) -> Unit,
    onPasswordChangeCallback: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(TEXT_FIELD_WIDTH_FACTOR)) {
        UsernameTextField(
            username = username,
            onUsernameChangeCallback = onUsernameChangeCallback,
            modifier = Modifier.padding(bottom = USERNAME_TO_PASSWORD_PADDING.dp)
        )

        PasswordTextField(
            password = password,
            onPasswordChangeCallback = onPasswordChangeCallback
        )
    }
}
