package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchViewModel.MedicineLoadingState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner


/**
 * Medicine screen.
 *
 * @param loadMoreMedicines callback to be invoked when the user clicks on the search medicine button
 * @param loadingState the current state of the loading operation
 */
@Composable
fun MedicineScreen(
    medicines: List<Medicine>,
    loadMoreMedicines: () -> Unit,
    loadingState: MedicineSearchViewModel.MedicineLoadingState
) {
    val listState = rememberLazyListState()
    val reachedBottom by remember {
        derivedStateOf {
            listState.reachedBottom()
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom && loadingState != MedicineSearchViewModel.MedicineLoadingState.LOADING) {
            loadMoreMedicines()
        }
    }

    PharmacistScreen {
        LazyColumn(state = listState) {
            items(items = medicines, key = { item ->
                item.id
            }) { medicine ->
                Box {
                    Row {
                        Text(
                            text = medicine.name,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(16.dp)
                        )

                    }
                }
            }
            if (loadingState == LOADING) {
                item {
                    Box {
                        Text(text = "Loading more medicines...", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}


private fun LazyListState.reachedBottom(): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - 1
}

//@Preview
//@Composable
//private fun MedicineScreenPreview() {
//    MedicineScreen(
//        onSearchMedicineClick = {},
//        loadingState = MedicineViewModel.MedicineLoadingState.LOADED
//    )
//}

