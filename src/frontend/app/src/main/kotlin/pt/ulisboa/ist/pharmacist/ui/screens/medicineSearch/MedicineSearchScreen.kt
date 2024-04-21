package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.service.services.medicines.models.getMedicinesWithClosestPharmacy.MedicineWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.components.MedicineEntry
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.LocationPermissionScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner

private const val TEXT_FIELD_WIDTH_FACTOR = 0.8f

/**
 * Medicine Search screen.
 */
@Composable
fun MedicineSearchScreen(
    hasLocationPermission: Boolean,
    medicinesState: Flow<PagingData<MedicineWithClosestPharmacyOutputModel>>,
    onSearch: (String) -> Unit,
    onMedicineClicked: (Long) -> Unit
) {
    val medicinePagingItems = medicinesState.collectAsLazyPagingItems()

    var query by remember { mutableStateOf("") }

    PharmacistScreen {
        var hasPermission by remember {
            mutableStateOf(hasLocationPermission)
        }

        if (!hasPermission) {
            LocationPermissionScreen(onPermissionGranted = { hasPermission = true })
            return@PharmacistScreen
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
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
                    Icon(Icons.Rounded.Search, contentDescription = "Search Button")
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth(TEXT_FIELD_WIDTH_FACTOR)) {
                items(medicinePagingItems.itemCount) { index ->
                    val (medicine, closestPharmacy) = medicinePagingItems[index]!!
                    MedicineEntry(medicine, closestPharmacy, onMedicineClicked)
                }

                medicinePagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                    ) {
                                        LoadingSpinner(modifier = Modifier.align(Alignment.Center))
                                    }
                                }
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val error = medicinePagingItems.loadState.refresh as LoadState.Error
                            item {
                                Box {
                                    Text(
                                        text = error.error.localizedMessage!!,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                    ) {
                                        LoadingSpinner(modifier = Modifier.align(Alignment.Center))
                                    }
                                }
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            val error = medicinePagingItems.loadState.append as LoadState.Error
                            item {
                                Box {
                                    Text(
                                        text = error.error.localizedMessage!!,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

