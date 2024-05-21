package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
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
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.StarRatingBar
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage
import pt.ulisboa.ist.pharmacist.ui.theme.Favorite

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onPharmacyDetailsClick(pharmacy.pharmacyId)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MeteredAsyncImage(
            url = pharmacy.pictureUrl,
            contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
            modifier = Modifier
        )
        Column(
            modifier = Modifier.align(Alignment.Top)
        ) {
            Text(
                text = pharmacy.name,
                style = MaterialTheme.typography.titleLarge
            )
            if (pharmacy.globalRating != null)
                Row {
                    Text(
                        text = String.format("%.1f", pharmacy.globalRating),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    StarRatingBar(
                        rating = pharmacy.globalRating.toInt(),
                        densityFactor = 7f
                    )
                }
            if (pharmacy.userMarkedAsFavorite)
                Row {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = stringResource(R.string.favorite_pharmacy),
                        tint = Favorite,
                    )
                    Text(
                        text = stringResource(R.string.favorite_pharmacy),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            Text(
                text = stringResource(R.string.pharmacyMap_clickForDetails_text),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light
            )

        }

    }
}