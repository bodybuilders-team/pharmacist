package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R

/**
 * Button to add a pharmacy to the map.
 *
 * @param addingPharmacy whether the user is currently adding a pharmacy
 * @param pickingOnMap whether the user is currently picking a location on the map
 * @param onClick callback to be invoked when the user clicks on the button
 */
@Composable
fun BoxScope.AddPharmacyButton(
    addingPharmacy: Boolean,
    pickingOnMap: Boolean,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(24.dp),
        icon = {
            Icon(
                imageVector = when {
                    pickingOnMap -> Icons.Rounded.CheckCircle
                    addingPharmacy -> Icons.Rounded.Cancel
                    else -> Icons.Rounded.Add
                },
                contentDescription = when {
                    pickingOnMap -> stringResource(R.string.choose_this_location)
                    addingPharmacy -> stringResource(R.string.pharmacyMap_cancel_button_description)
                    else -> stringResource(R.string.pharmacyMap_addPharmacy_button_description)
                }
            )
        },
        text = {
            Text(
                when {
                    pickingOnMap -> stringResource(R.string.choose_this_location)
                    addingPharmacy -> stringResource(R.string.pharmacyMap_cancel_button_description)
                    else -> stringResource(R.string.pharmacyMap_addPharmacy_button_description)
                }
            )
        }
    )
}