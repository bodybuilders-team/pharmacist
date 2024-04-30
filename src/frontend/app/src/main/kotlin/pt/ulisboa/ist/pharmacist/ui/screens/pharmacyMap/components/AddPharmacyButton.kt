package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R

/**
 * Button to add a pharmacy to the map.
 *
 * @param addingPharmacy whether the user is currently adding a pharmacy
 * @param onClick callback to be invoked when the user clicks on the button
 */
@Composable
fun BoxScope.AddPharmacyButton(
    addingPharmacy: Boolean,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(24.dp),
        icon = {
            Icon(
                if (!addingPharmacy) Icons.Rounded.Add else Icons.Rounded.Cancel,
                if (!addingPharmacy)
                    stringResource(R.string.pharmacyMap_addPharmacy_button_text)
                else
                    stringResource(R.string.pharmacyMap_cancel_button_text),
            )
        },
        text = {
            Text(
                if (!addingPharmacy)
                    stringResource(R.string.pharmacyMap_addPharmacy_button_description)
                else
                    stringResource(R.string.pharmacyMap_cancel_button_description)
            )
        }
    )
}