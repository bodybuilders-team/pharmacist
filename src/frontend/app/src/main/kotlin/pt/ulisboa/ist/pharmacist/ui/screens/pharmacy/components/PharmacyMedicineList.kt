package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components

import androidx.compose.foundation.layout.Box
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineStock
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner

@Composable
fun PharmacyMedicineList(
    medicineList: LazyPagingItems<MedicineStock>,
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
            text = "${medicineList.itemCount} " +
                    stringResource(
                        if (medicineList.itemCount != 1)
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

        when (medicineList.loadState.refresh) {
            is LoadState.Loading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                LoadingSpinner(modifier = Modifier.align(Alignment.Center))
            }

            else ->
                LazyColumn(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    items(medicineList.itemCount) { index ->
                        val (medicine, stock) = medicineList[index]!!
                        PharmacyMedicineEntry(
                            medicine,
                            stock,
                            onMedicineClick = onMedicineClick,
                            onAddStockClick = onAddStockClick,
                            onRemoveStockClick = onRemoveStockClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    when (medicineList.loadState.append) {
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