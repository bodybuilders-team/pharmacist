package pt.ulisboa.ist.pharmacist.ui.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.home.BUTTON_MAX_WIDTH_FACTOR
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton

@Composable
fun HomeButtons(
    loggedIn: Boolean,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onContinueAsGuestClick: () -> Unit,
    onPharmacyMapClick: () -> Unit,
    onSearchMedicineClick: () -> Unit,
    isGuest: Boolean,
    onUpgradeAccountClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Column {
        if (!loggedIn) {
            IconTextButton(
                onClick = onLoginClick,
                imageVector = Icons.AutoMirrored.Rounded.Login,
                contentDescription = stringResource(R.string.home_login_button_description),
                text = stringResource(R.string.home_login_button_text),
                modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
            )

            IconTextButton(
                onClick = onRegisterClick,
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = stringResource(R.string.home_register_button_description),
                text = stringResource(R.string.home_register_button_text),
                modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
            )

            IconTextButton(
                onClick = { onContinueAsGuestClick() },
                imageVector = Icons.Rounded.Person,
                contentDescription = stringResource(R.string.continue_as_guest),
                text = stringResource(R.string.continue_as_guest),
                modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
            )
        } else {
            IconTextButton(
                onClick = onPharmacyMapClick,
                imageVector = Icons.Rounded.Map,
                contentDescription = stringResource(R.string.home_pharmacy_map_button_description),
                text = stringResource(R.string.home_pharmacy_map_button_text),
                modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
            )

            IconTextButton(
                onClick = onSearchMedicineClick,
                imageVector = Icons.Rounded.Search,
                contentDescription = stringResource(R.string.home_search_medicine_button_description),
                text = stringResource(R.string.home_search_medicine_button_text),
                modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
            )

            if (isGuest)
                IconTextButton(
                    onClick = onUpgradeAccountClick,
                    imageVector = Icons.Rounded.Upgrade,
                    contentDescription = stringResource(R.string.upgrade_account),
                    text = stringResource(R.string.upgrade_account),
                    modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
                )

            IconTextButton(
                onClick = onLogoutClick,
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = stringResource(R.string.home_logout_button_description),
                text = stringResource(R.string.home_logout_button_text),
                modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
            )
        }

        IconTextButton(
            onClick = onAboutClick,
            imageVector = Icons.Rounded.Info,
            contentDescription = stringResource(R.string.home_about_button_description),
            text = stringResource(R.string.home_about_button_text),
            modifier = Modifier.fillMaxWidth(BUTTON_MAX_WIDTH_FACTOR)
        )
    }
}