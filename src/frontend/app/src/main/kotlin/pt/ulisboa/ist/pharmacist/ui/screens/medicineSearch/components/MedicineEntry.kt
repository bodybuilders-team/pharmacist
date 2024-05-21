package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage
import kotlin.math.min

/**
 * A medicine entry in the search medicine result list.
 *
 * @param medicine the MedicineWithClosestPharmacy to be displayed
 * @param onMedicineClicked function to be executed when the medicine is clicked
 * @param isSelected true if the medicine is selected, false otherwise
 */
@Composable
fun MedicineEntry(
    medicine: MedicineWithClosestPharmacy,
    onMedicineClicked: (MedicineWithClosestPharmacy) -> Unit,
    isSelected: Boolean,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .background(
                when {
                    isSelected -> Color.Gray
                    else -> MaterialTheme.colorScheme.secondary
                },
                shape = MaterialTheme.shapes.medium
            )
            .clickable {
                onMedicineClicked(medicine)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 8.dp)
        ) {
            MeteredAsyncImage(
                url = medicine.boxPhotoUrl,
                contentDescription = stringResource(R.string.medicine_boxPhoto_description),
                modifier = Modifier
                    .width(100.dp)
                    .align(Alignment.CenterVertically)
                    .padding(end = 4.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = medicine.description.substring(0, min(100, medicine.description.length))
                        .plus(if (medicine.description.length > 100) "..." else ""),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.1f)
                )
                if (medicine.closestPharmacy != null)
                    Text(
                        text = "pharmacyId=${medicine.closestPharmacy}", // TODO: Need pharmacy name
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.1f)
                    )
                else
                    Text(
                        text = stringResource(R.string.no_pharmacy_available),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.1f)
                    )
            }
        }
    }
}