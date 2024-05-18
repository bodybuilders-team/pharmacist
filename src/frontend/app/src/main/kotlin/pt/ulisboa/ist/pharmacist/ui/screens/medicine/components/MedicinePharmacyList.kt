package pt.ulisboa.ist.pharmacist.ui.screens.medicine.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel

@Composable
fun MedicinePharmacyList(
    pharmacies: LazyPagingItems<PharmacyWithUserDataModel>,
    onPharmacyClick: (Pharmacy) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (pharmacies.itemCount != 1) stringResource(
                R.string.medicine_availableInXPharmacies_text,
                pharmacies.itemCount
            ) else
                stringResource(R.string.medicine_availableIn1Pharmacy_text),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
        ) {
            items(pharmacies.itemCount) { index ->
                val pharmacy = pharmacies[index]!!
                MedicinePharmacyEntry(
                    pharmacy,
                    onPharmacyClick = onPharmacyClick
                )
            }
        }
    }
}