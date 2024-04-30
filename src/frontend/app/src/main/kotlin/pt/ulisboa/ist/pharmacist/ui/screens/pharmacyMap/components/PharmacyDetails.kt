package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage

/**
 * Component to display the details of a pharmacy on the map.
 *
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 * @param pharmacy the pharmacy to display
 */
@Composable
fun PharmacyDetails(
    onPharmacyDetailsClick: (Long) -> Unit,
    pharmacy: Pharmacy
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
            .clickable {
                onPharmacyDetailsClick(pharmacy.pharmacyId)
            }
    ) {
        Text(
            text = pharmacy.name,
            style = MaterialTheme.typography.titleLarge
        )
        if (pharmacy.globalRating != null)
            Text(
                text = "${
                    String.format("%.1f", pharmacy.globalRating)
                } ⭐ (${pharmacy.numberOfRatings.sum()})",
                style = MaterialTheme.typography.bodyLarge
            )
        Text(
            text = stringResource(R.string.pharmacyMap_clickForDetails_text),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Light
        )
        MeteredAsyncImage(
            url = pharmacy.pictureUrl,
            contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}