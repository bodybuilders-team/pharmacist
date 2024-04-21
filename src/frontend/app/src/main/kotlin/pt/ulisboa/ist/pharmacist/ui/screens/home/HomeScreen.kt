package pt.ulisboa.ist.pharmacist.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconButton

private const val LOGO_MAX_WIDTH_FACTOR = 0.6f
private const val LOGO_MAX_HEIGHT_FACTOR = 0.5f
private const val BUTTON_MAX_WIDTH_FACTOR = 0.6f

private const val WELCOME_TEXT_PADDING = 14
private const val WELCOME_TEXT_WIDTH_FACTOR = 0.9f

/**
 * Home screen.
 *
 * @param loggedIn if true, the user is logged in
 * @param username the username of the logged in user
 * @param onLoginClick callback to be invoked when the user clicks on the login button
 * @param onRegisterClick callback to be invoked when the user clicks on the register button
 * @param onContinueAsGuestClick callback to be invoked when the user clicks on the continue as guest button
 * @param onLogoutClick callback to be invoked when the user clicks on the logout button
 * @param onPharmacyMapClick callback to be invoked when the user clicks on the pharmacy map button
 * @param onSearchMedicineClick callback to be invoked when the user clicks on the search medicine button
 * @param onAboutClick callback to be invoked when the user clicks on the about button
 */
@Composable
fun HomeScreen(
    loggedIn: Boolean,
    username: String?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onContinueAsGuestClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPharmacyMapClick: () -> Unit,
    onSearchMedicineClick: () -> Unit,
) {
    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Box {
                Image(
                    painter = painterResource(R.drawable.pharmacy_logo),
                    contentDescription = stringResource(R.string.logo_content_description),
                    modifier = Modifier
                        .fillMaxWidth(LOGO_MAX_WIDTH_FACTOR)
                        .fillMaxHeight(LOGO_MAX_HEIGHT_FACTOR)
                )
            }

            Text(
                text = if (loggedIn)
                    stringResource(R.string.home_welcome_text, username!!)
                else
                    stringResource(R.string.home_welcome_guest_text),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(WELCOME_TEXT_WIDTH_FACTOR)
                    .padding(bottom = WELCOME_TEXT_PADDING.dp)
            )


            if (!loggedIn) {
                IconButton(
                    onClick = onLoginClick,
                    painter = painterResource(R.drawable.ic_round_login_24),
                    contentDescription = stringResource(R.string.home_loginButton_description),
                    text = stringResource(R.string.home_loginButton_text),
                    modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
                )

                IconButton(
                    onClick = onRegisterClick,
                    painter = painterResource(R.drawable.ic_round_person_add_24),
                    contentDescription = stringResource(R.string.home_registerButton_description),
                    text = stringResource(R.string.home_registerButton_text),
                    modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
                )

                IconButton(
                    onClick = onContinueAsGuestClick,
                    painter = painterResource(R.drawable.round_person_24),
                    contentDescription = "Continue as guest",
                    text = "Continue as Guest",
                    modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
                )
            } else {
                IconButton(
                    onClick = onLogoutClick,
                    painter = painterResource(R.drawable.ic_round_logout_24),
                    contentDescription = stringResource(R.string.home_logoutButton_description),
                    text = stringResource(R.string.home_logoutButton_text),
                    modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
                )

                IconButton(
                    onClick = onPharmacyMapClick,
                    painter = painterResource(R.drawable.round_map_24),
                    contentDescription = stringResource(R.string.home_pharmacyMapButton_description),
                    text = stringResource(R.string.home_pharmacyMapButton_text),
                    modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
                )

                IconButton(
                    onClick = onSearchMedicineClick,
                    painter = painterResource(R.drawable.ic_round_search_24),
                    contentDescription = stringResource(R.string.home_searchMedicineButton_description),
                    text = stringResource(R.string.home_searchMedicineButton_text),
                    modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
                )
            }

            IconButton(
                onClick = onAboutClick,
                painter = painterResource(R.drawable.ic_round_info_24),
                contentDescription = stringResource(R.string.home_aboutButton_description),
                text = stringResource(R.string.home_aboutButton_text),
                modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
            )
        }
    }
}
