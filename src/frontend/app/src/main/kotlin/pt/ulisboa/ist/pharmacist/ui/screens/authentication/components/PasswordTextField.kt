package pt.ulisboa.ist.pharmacist.ui.screens.authentication.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.MAX_PASSWORD_LENGTH
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.validatePassword

/**
 * The password text field.
 *
 * @param password password to show
 * @param onPasswordChangeCallback callback to be invoked when the password text is changed
 */
@Composable
fun PasswordTextField(
    password: String,
    onPasswordChangeCallback: (String) -> Unit
) {
    val invalidPasswordMessage = stringResource(R.string.authentication_message_invalid_password)
    val invalidPassword = password.isNotEmpty() && !validatePassword(password)

    AuthenticationTextField(
        label = stringResource(R.string.authentication_password_text_field_label),
        value = password,
        onValueChange = onPasswordChangeCallback,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        required = true,
        maxLength = MAX_PASSWORD_LENGTH,
        errorMessage = if (invalidPassword) invalidPasswordMessage else null
    )
}
