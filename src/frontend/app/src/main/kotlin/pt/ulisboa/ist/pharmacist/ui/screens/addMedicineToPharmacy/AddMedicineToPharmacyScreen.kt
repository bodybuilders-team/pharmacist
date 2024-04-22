package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.services.medicines.models.getMedicinesWithClosestPharmacy.MedicineWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearch
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyMedicineEntry
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton
import pt.ulisboa.ist.pharmacist.ui.theme.PharmacistTheme


/**
 * Screen for adding a medicine to a pharmacy.
 */
@Composable
fun AddMedicineToPharmacyScreen(
    hasLocationPermission: Boolean,
    medicinesState: Flow<PagingData<MedicineWithClosestPharmacyOutputModel>>,
    onSearch: (String) -> Unit,
    onMedicineClicked: (Medicine) -> Unit,
    selectedMedicine: Medicine?,
    createMedicine: () -> Unit,
    addMedicineToPharmacy: (Long, Long) -> Unit,
) {
    PharmacistTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Available Medicines",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )
            MedicineSearch(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.8f),
                hasLocationPermission = hasLocationPermission,
                medicinesState = medicinesState,
                onSearch = onSearch,
                onMedicineClicked = onMedicineClicked,
                selectedMedicine = selectedMedicine
            )

            var stock by remember { mutableLongStateOf(0L) }

            if (selectedMedicine != null) {
                PharmacyMedicineEntry(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    medicine = selectedMedicine,
                    stock = stock,
                    onAddStockClick = {
                        stock += 1
                    },
                    onRemoveStockClick = {
                        stock -= 1
                    }
                )
            }

            IconTextButton(
                onClick = createMedicine,
                imageVector = Icons.Rounded.Add,
                text = "Create Medicine",
                contentDescription = "Create Medicine",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            IconTextButton(
                onClick = {
                    if (selectedMedicine != null)
                        addMedicineToPharmacy(selectedMedicine.medicineId, stock)
                },
                enabled = selectedMedicine != null,
                imageVector = Icons.Rounded.Add,
                text = "Add Medicine To Pharmacy",
                contentDescription = "Add Medicine To Pharmacy",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

