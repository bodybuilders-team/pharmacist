package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineStock
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton

@Composable
fun PharmacyMedicineList(
    medicinesStock: List<MedicineStock>,
    onAddMedicineClick: () -> Unit,
    onMedicineClick: (Long) -> Unit,
    onAddStockClick: (Long) -> Unit,
    onRemoveStockClick: (Long) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "${medicinesStock.size} " +
                    stringResource(
                        if (medicinesStock.size != 1)
                            R.string.medicines_available
                        else R.string.medicine_available
                    ),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(8.dp)
        )

        IconTextButton(
            imageVector = Icons.Rounded.Medication,
            text = stringResource(R.string.add_medicine),
            contentDescription = stringResource(R.string.add_medicine),
            onClick = onAddMedicineClick
        )

        LazyColumn(
            modifier = Modifier
                .padding(20.dp)
        ) {
            items(medicinesStock.size) { index ->
                val (medicine, stock) = medicinesStock[index]
                PharmacyMedicineEntry(
                    medicine,
                    stock,
                    onMedicineClick = onMedicineClick,
                    onAddStockClick = onAddStockClick,
                    onRemoveStockClick = onRemoveStockClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}