package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.components.MedicineEntry
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.PermissionScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner

private const val TEXT_FIELD_WIDTH_FACTOR = 0.8f

/**
 * Medicine Search screen.
 */
@Composable
fun MedicineSearchScreen(
    hasLocationPermission: Boolean,
    medicinePagingItems: LazyPagingItems<MedicineWithClosestPharmacy>?,
    onSearch: (String) -> Unit,
    onMedicineClicked: (MedicineWithClosestPharmacy) -> Unit,
) {
    PharmacistScreen {
        var rememberedHasLocationPermission by remember { mutableStateOf(hasLocationPermission) }

        if (!rememberedHasLocationPermission) {
            PermissionScreen(
                onPermissionGranted = {
                    rememberedHasLocationPermission = true
                }, permissionRequests = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                permissionTitle = stringResource(R.string.pharmacy_map_location_permission_title),
                settingsPermissionNote = stringResource(R.string.pharmacyMap_location_permission_note),
                settingsPermissionNoteButtonText = stringResource(R.string.permission_settings_button)
            )
            return@PharmacistScreen
        }

        MedicineSearch(
            modifier = Modifier.fillMaxSize(),
            medicinePagingItems = medicinePagingItems,
            onSearch = onSearch,
            onMedicineClicked = onMedicineClicked
        )
    }
}

@Composable
fun MedicineSearch(
    modifier: Modifier = Modifier,
    medicinePagingItems: LazyPagingItems<MedicineWithClosestPharmacy>?,
    onSearch: (String) -> Unit,
    onMedicineClicked: (MedicineWithClosestPharmacy) -> Unit,
    selectedMedicine: MedicineWithClosestPharmacy? = null
) {
    var query by remember { mutableStateOf("") }

    if (medicinePagingItems == null)
        return Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LoadingSpinner(modifier = Modifier.align(Alignment.Center))
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(TEXT_FIELD_WIDTH_FACTOR)
                .padding(top = 16.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            )
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { onSearch(query) }
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.search_button)
                )
            }
        }

        when (medicinePagingItems.loadState.refresh) {
            is LoadState.Loading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                LoadingSpinner(modifier = Modifier.align(Alignment.Center))
            }

            else -> LazyColumn(modifier = Modifier.fillMaxWidth(TEXT_FIELD_WIDTH_FACTOR)) {
                items(count = medicinePagingItems.itemCount)
                { index ->
                    medicinePagingItems[index]?.let { medicine ->
                        MedicineEntry(
                            medicine = medicine,
                            onMedicineClicked = { onMedicineClicked(medicine) },
                            isSelected = selectedMedicine?.medicineId == medicine.medicineId
                        )
                    }
                }

                when (medicinePagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            LoadingSpinner(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

