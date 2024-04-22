package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton

private const val BUTTON_PADDING = 8

/**
 * Button for login operation.
 *
 * @param enabled whether the button is enabled or not
 * @param onLoginClickCallback callback to be invoked when the login button is clicked
 */
@Composable
fun LoginButton(
    enabled: Boolean = true,
    onLoginClickCallback: () -> Unit
) {
    IconTextButton(
        onClick = onLoginClickCallback,
        enabled = enabled,
        modifier = Modifier.padding(BUTTON_PADDING.dp),
        text = stringResource(R.string.login_loginButton_text),
        imageVector = Icons.AutoMirrored.Rounded.Login,
        contentDescription = stringResource(R.string.login_loginButton_description)
    )
}
