package pt.ulisboa.ist.pharmacist.ui.screens.authentication.upgrade.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton

private const val BUTTON_PADDING = 8

/**
 * Button for login operation.
 *
 * @param enabled whether the button is enabled or not
 * @param onUpgradeAccountClick callback to be invoked when the login button is clicked
 */
@Composable
fun UpgradeButton(
    enabled: Boolean = true,
    onUpgradeAccountClick: () -> Unit
) {
    IconTextButton(
        onClick = onUpgradeAccountClick,
        enabled = enabled,
        imageVector = Icons.Rounded.Upgrade,
        contentDescription = "Upgrade account",
        text = "Upgrade account",
        modifier = Modifier.padding(BUTTON_PADDING.dp),
    )
}
