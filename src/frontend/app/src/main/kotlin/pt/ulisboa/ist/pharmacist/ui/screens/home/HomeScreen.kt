package pt.ulisboa.ist.pharmacist.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.home.components.HomeButtons
import pt.ulisboa.ist.pharmacist.ui.screens.home.components.HomeHeader

const val LOGO_MAX_WIDTH_FACTOR = 0.6f
const val LOGO_MAX_HEIGHT_FACTOR = 0.5f
const val BUTTON_MAX_WIDTH_FACTOR = 0.6f

const val WELCOME_TEXT_PADDING = 14
const val WELCOME_TEXT_WIDTH_FACTOR = 0.9f

/**
 * Home screen.
 *
 * @param loggedIn if true, the user is logged in
 * @param isGuest if true, the user is a guest
 * @param username the username of the logged in user
 * @param onLoginClick callback to be invoked when the user clicks on the login button
 * @param onRegisterClick callback to be invoked when the user clicks on the register button
 * @param onContinueAsGuestClick callback to be invoked when the user clicks on the continue as guest button
 * @param onUpgradeAccountClick callback to be invoked when the user clicks on the upgrade account button
 * @param onLogoutClick callback to be invoked when the user clicks on the logout button
 * @param onPharmacyMapClick callback to be invoked when the user clicks on the pharmacy map button
 * @param onSearchMedicineClick callback to be invoked when the user clicks on the search medicine button
 * @param onAboutClick callback to be invoked when the user clicks on the about button
 */
@Composable
fun HomeScreen(
    loggedIn: Boolean,
    isGuest: Boolean,
    username: String?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onContinueAsGuestClick: () -> Unit,
    onUpgradeAccountClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPharmacyMapClick: () -> Unit,
    onSearchMedicineClick: () -> Unit,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    PharmacistScreen {
        if (isLandscape)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                HomeHeader(loggedIn, username, Modifier.weight(1f))
                HomeButtons(
                    loggedIn,
                    onLoginClick,
                    onRegisterClick,
                    onContinueAsGuestClick,
                    onPharmacyMapClick,
                    onSearchMedicineClick,
                    isGuest,
                    onUpgradeAccountClick,
                    onLogoutClick,
                    onAboutClick
                )
            }
        else
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                HomeHeader(loggedIn, username)
                HomeButtons(
                    loggedIn,
                    onLoginClick,
                    onRegisterClick,
                    onContinueAsGuestClick,
                    onPharmacyMapClick,
                    onSearchMedicineClick,
                    isGuest,
                    onUpgradeAccountClick,
                    onLogoutClick,
                    onAboutClick
                )
            }
    }
}