package pt.ulisboa.ist.pharmacist.ui.screens.medicine.components

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.StarRatingBar
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage
import pt.ulisboa.ist.pharmacist.ui.theme.Favorite

/**
 * A pharmacy entry in the medicine pharmacy list.
 *
 * @param pharmacy the Pharmacy
 * @param onPharmacyClick function to be executed when the pharmacy is clicked
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MedicinePharmacyEntry(
    pharmacy: PharmacyWithUserDataModel,
    onPharmacyClick: (Pharmacy) -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .padding(bottom = 8.dp)
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_HOVER_ENTER -> isHovered = true
                MotionEvent.ACTION_HOVER_EXIT -> isHovered = false
            }
            false
        }
        .background(
            color = if (isHovered) Color.LightGray else MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium
        )
        .clickable {
            onPharmacyClick(pharmacy.pharmacy)
        }) {
        MeteredAsyncImage(
            url = pharmacy.pharmacy.pictureUrl,
            contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
            modifier = Modifier
                .width(100.dp)
                .align(Alignment.CenterVertically)
                .padding(start = 4.dp, end = 4.dp)
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
                    StarRatingBar(rating = pharmacy.pharmacy.globalRating.toInt(), densityFactor = 6f)
                }
            if (pharmacy.userMarkedAsFavorite)
                Row {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = Favorite,
                    )
                    Text(
                        text = "Favorite Pharmacy",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
        }
    }
}