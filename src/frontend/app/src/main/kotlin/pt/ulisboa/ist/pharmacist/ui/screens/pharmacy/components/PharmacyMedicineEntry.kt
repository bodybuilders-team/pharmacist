package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import kotlin.math.min
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.ui.utils.MeteredAsyncImage

/**
 * A medicine entry in the pharmacy medicine list.
 *
 * @param medicine the Medicine
 * @param stock the stock of the medicine
 * @param onMedicineClick function to be executed when the medicine is clicked
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PharmacyMedicineEntry(
    medicine: Medicine,
    stock: Long,
    onMedicineClick: (Long) -> Unit,
    onAddStockClick: (Long) -> Unit,
    onRemoveStockClick: (Long) -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(bottom = 8.dp)
            .background(
                color = Color(0xAAE0E0E0),
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
            .weight(0.9f)
            .background(if (isHovered) Color.LightGray else Color.Transparent)
            .clickable {
                onMedicineClick(medicine.medicineId)
            }) {
            MeteredAsyncImage(
                url = medicine.boxPhotoUrl,
                contentDescription = "Pharmacy picture",
                modifier = Modifier
                    .width(100.dp)
                    .align(Alignment.CenterVertically)
                    .padding(end = 4.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                )
                Text(
                    text = medicine.description.substring(
                        0,
                        min(100, medicine.description.length)
                    )
                        .plus(if (medicine.description.length > 100) "..." else ""),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.1f)
                )
            }
        }
        Column(
            modifier = Modifier
                .width(40.dp)
                .height(200.dp)
                .align(Alignment.CenterVertically)
        ) {
            IconButton(
                onClick = { onAddStockClick(medicine.medicineId) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add stock",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "$stock",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            IconButton(
                onClick = { onRemoveStockClick(medicine.medicineId) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    Icons.Rounded.Remove,
                    contentDescription = "Remove stock",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}