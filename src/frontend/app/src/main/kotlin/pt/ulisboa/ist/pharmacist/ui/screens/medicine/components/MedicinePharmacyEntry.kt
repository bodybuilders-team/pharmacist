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
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage

/**
 * A pharmacy entry in the medicine pharmacy list.
 *
 * @param pharmacy the Pharmacy
 * @param onPharmacyClick function to be executed when the pharmacy is clicked
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MedicinePharmacyEntry(
    pharmacy: Pharmacy,
    onPharmacyClick: (Pharmacy) -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(bottom = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Row(modifier = Modifier
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_HOVER_ENTER -> isHovered = true
                    MotionEvent.ACTION_HOVER_EXIT -> isHovered = false
                }
                false
            }
            .align(Alignment.CenterVertically)
            .background(if (isHovered) Color.LightGray else Color.Transparent)
            .clickable {
                onPharmacyClick(pharmacy)
            }) {
            MeteredAsyncImage(
                url = pharmacy.pictureUrl,
                contentDescription = "Pharmacy picture",
                modifier = Modifier
                    .width(100.dp)
                    .align(Alignment.CenterVertically)
                    .padding(start = 4.dp, end = 4.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Top)
            ) {
                Text(
                    text = pharmacy.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                )
                Text(
                    text = "📍 ${pharmacy.location}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (pharmacy.globalRating != null)
                    Text(
                        text = "Rating: ${String.format("%.1f", pharmacy.globalRating)} ⭐",
                        style = MaterialTheme.typography.bodySmall
                    )
            }
        }
    }
}