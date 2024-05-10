package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.models.getMedicinesWithClosestPharmacy.MedicineWithClosestPharmacyOutputModel
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
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var stock by remember { mutableLongStateOf(0L) }

    PharmacistTheme {
        if (isLandscape)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                MedicineSelector(
                    hasLocationPermission,
                    medicinesState,
                    onSearch,
                    onMedicineClicked,
                    selectedMedicine,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )

                SelectedMedicine(
                    selectedMedicine,
                    stock,
                    addMedicineToPharmacy,
                    createMedicine,
                    onAddStockClick = {
                        stock += 1
                    },
                    onRemoveStockClick = {
                        stock -= 1
                    }
                )
            }
        else
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MedicineSelector(
                    hasLocationPermission,
                    medicinesState,
                    onSearch,
                    onMedicineClicked,
                    selectedMedicine,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                )

                SelectedMedicine(
                    selectedMedicine,
                    stock,
                    addMedicineToPharmacy,
                    createMedicine,
                    onAddStockClick = {
                        stock += 1
                    },
                    onRemoveStockClick = {
                        stock -= 1
                    }
                )
            }
    }
}

@Composable
private fun SelectedMedicine(
    selectedMedicine: Medicine?,
    stock: Long,
    addMedicineToPharmacy: (Long, Long) -> Unit,
    createMedicine: () -> Unit,
    onAddStockClick: (Long) -> Unit,
    onRemoveStockClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedMedicine != null)
            PharmacyMedicineEntry(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                medicine = selectedMedicine,
                stock = stock,
                onAddStockClick = onAddStockClick,
                onRemoveStockClick = onRemoveStockClick
            )
        else
            Text(
                text = stringResource(R.string.no_medicine_selected),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
            )

        IconTextButton(
            onClick = {
                if (selectedMedicine != null)
                    addMedicineToPharmacy(selectedMedicine.medicineId, stock)
            },
            enabled = selectedMedicine != null,
            imageVector = Icons.Rounded.AddCircleOutline,
            text = stringResource(R.string.add_medicine_to_pharmacy),
            contentDescription = stringResource(R.string.add_medicine_to_pharmacy),
            modifier = Modifier
        )

        IconTextButton(
            onClick = createMedicine,
            imageVector = Icons.Rounded.Medication,
            text = stringResource(R.string.create_medicine),
            contentDescription = stringResource(R.string.create_medicine),
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun MedicineSelector(
    hasLocationPermission: Boolean,
    medicinesState: Flow<PagingData<MedicineWithClosestPharmacyOutputModel>>,
    onSearch: (String) -> Unit,
    onMedicineClicked: (Medicine) -> Unit,
    selectedMedicine: Medicine?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.available_medicines),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(top = 16.dp)
        )
        MedicineSearch(
            modifier = Modifier
                .weight(0.8f),
            hasLocationPermission = hasLocationPermission,
            medicinesState = medicinesState,
            onSearch = onSearch,
            onMedicineClicked = onMedicineClicked,
            selectedMedicine = selectedMedicine
        )
    }
}

