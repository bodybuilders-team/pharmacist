package pt.ulisboa.ist.pharmacist.ui.screens.medicine.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.StarRatingBar
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage
import pt.ulisboa.ist.pharmacist.ui.theme.Favorite

/**
 * A pharmacy entry in the medicine pharmacy list.
 *
 * @param pharmacy the Pharmacy
 * @param onPharmacyClick function to be executed when the pharmacy is clicked
 */
@Composable
fun MedicinePharmacyEntry(
    pharmacy: PharmacyWithUserDataModel,
    onPharmacyClick: (Pharmacy) -> Unit
) {
    ElevatedCard(modifier = Modifier
        .height(120.dp)
        .padding(bottom = 8.dp)
        .background(
            color = MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium
        )
        .clickable {
            onPharmacyClick(pharmacy.pharmacy)
        }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            MeteredAsyncImage(
                url = pharmacy.pharmacy.pictureUrl,
                contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
                modifier = Modifier
                    .width(100.dp)
                    .padding(start = 4.dp, end = 4.dp)
                    .align(Alignment.CenterVertically)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.Top)
            ) {
                Text(
                    text = pharmacy.pharmacy.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                )
                if (pharmacy.pharmacy.globalRating != null)
                    Row {
                        Text(
                            text = String.format("%.1f", pharmacy.pharmacy.globalRating),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        StarRatingBar(
                            rating = pharmacy.pharmacy.globalRating.toInt(),
                            densityFactor = 6f
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
            }
        }
    }
}