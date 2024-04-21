package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicineWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.LocationPermissionScreen
import pt.ulisboa.ist.pharmacist.ui.utils.MeteredAsyncImage


/**
 * Medicine Search screen.
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
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

        Column {
            Row {
                TextField(value = query, onValueChange = { query = it })
                Button(onClick = { onSearch(query) }) {
                    Text(text = "Search")
                }
            }


            LazyColumn {

                items(medicinePagingItems.itemCount) { index ->
                    val (medicine, closestPharmacy) = medicinePagingItems[index]!!

                    var isHovered by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .pointerInteropFilter {
                                when (it.action) {
                                    MotionEvent.ACTION_HOVER_ENTER -> isHovered = true
                                    MotionEvent.ACTION_HOVER_EXIT -> isHovered = false
                                }
                                false
                            }
                            .background(if (isHovered) Color.LightGray else Color.Transparent)
                            .clickable {
                                onMedicineClicked(medicine.medicineId)
                            }
                    ) {
                        Row {
                            MeteredAsyncImage(
                                url = medicine.boxPhotoUrl,
                                contentDescription = "Pharmacy picture",
                                modifier = Modifier.weight(0.1f)
                            )
                            Text(
                                text = medicine.name,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.weight(0.1f)
                            )
                            if (closestPharmacy != null) {
                                Text(
                                    text = closestPharmacy.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(0.1f)
                                )
                            } else
                                Text(
                                    text = "No pharmacy available",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(0.1f)
                                )
                        }
                    }
                }

                medicinePagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                Box {
                                    Text(
                                        text = "Loading medicines...",
                                        modifier = Modifier.padding(16.dp)
                                    )
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
                                    Text(
                                        text = "Loading more medicines...",
                                        modifier = Modifier.padding(16.dp)
                                    )
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

