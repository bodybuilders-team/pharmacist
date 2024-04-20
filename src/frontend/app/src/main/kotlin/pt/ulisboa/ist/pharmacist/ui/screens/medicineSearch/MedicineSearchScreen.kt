package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicineWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchViewModel.MedicineLoadingState.LOADING
import pt.ulisboa.ist.pharmacist.ui.utils.InfiniteScrollHandler
import pt.ulisboa.ist.pharmacist.ui.utils.MeteredAsyncImage


/**
 * Medicine Search screen.
 *
 * @param loadMoreMedicines callback to be invoked when the user clicks on the search medicine button
 * @param loadingState the current state of the loading operation
 */
@Composable
fun MedicineSearchScreen(
    medicines: List<MedicineWithClosestPharmacyOutputModel>,
    loadMoreMedicines: () -> Unit,
    onSearch: (String) -> Unit,
    loadingState: MedicineSearchViewModel.MedicineLoadingState
) {
    val listState = rememberLazyListState()
    var searchValue by remember { mutableStateOf("") }

    InfiniteScrollHandler(
        listState = listState,
        loadMore = {
            loadMoreMedicines()
        }
    )

    PharmacistScreen {
        Column {
            Row {
                TextField(value = searchValue, onValueChange = { searchValue = it })
                Button(onClick = { onSearch(searchValue) }) {
                    Text(text = "Search")
                }
            }

            LazyColumn(state = listState) {
                items(items = medicines, key = { item ->
                    item.medicine.medicineId
                }) { (medicine, closestPharmacy) ->
                    Box {
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
                                Text(
                                    text = closestPharmacy.location.toString(),
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


                if (loadingState == LOADING) {
                    item {
                        Box {
                            Text(
                                text = "Loading more medicines...",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

