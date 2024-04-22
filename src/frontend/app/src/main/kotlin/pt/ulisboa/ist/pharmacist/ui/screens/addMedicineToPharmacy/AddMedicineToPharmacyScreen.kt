package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.services.medicines.models.getMedicinesWithClosestPharmacy.MedicineWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearch
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyMedicineEntry
import pt.ulisboa.ist.pharmacist.ui.theme.PharmacistTheme


/**
 * Medicine screen.
 *
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
        Column {

            MedicineSearch(
                modifier = Modifier.weight(0.5f),
                hasLocationPermission = hasLocationPermission,
                medicinesState = medicinesState,
                onSearch = onSearch,
                onMedicineClicked = onMedicineClicked,
                selectedMedicine = selectedMedicine
            )

            var stock by remember { mutableStateOf(0L) }

            if (selectedMedicine != null) {
                PharmacyMedicineEntry(
                    selectedMedicine,
                    stock,
                    onAddStockClick = {
                        stock += 1
                    },
                    onRemoveStockClick = {
                        stock -= 1
                    }
                )
            }

            Button(
                modifier = Modifier
                    .weight(0.3f),
                onClick = {
                    createMedicine()
                }
            ) {
                Text("Create Medicine")
            }

            Button(
                modifier = Modifier
                    .weight(0.3f),
                enabled = selectedMedicine != null,
                onClick = {
                    if (selectedMedicine != null)
                        addMedicineToPharmacy(selectedMedicine.medicineId, stock)
                }
            ) {
                Text("Add Medicine To Pharmacy")
            }
        }

    }
}

