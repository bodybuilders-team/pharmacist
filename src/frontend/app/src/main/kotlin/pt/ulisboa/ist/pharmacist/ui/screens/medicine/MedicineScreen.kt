package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner
import pt.ulisboa.ist.pharmacist.ui.utils.MeteredAsyncImage


/**
 * Medicine screen.
 *
 */
@Composable
fun MedicineSearchScreen(
    medicine: Medicine?,
    loadingState: MedicineViewModel.MedicineLoadingState,
    pharmaciesState: Flow<PagingData<Pharmacy>>,
    onPharmacyClick: (Pharmacy) -> Unit
) {

    if (loadingState == MedicineViewModel.MedicineLoadingState.LOADED && medicine != null) {
        val pharmacies = pharmaciesState.collectAsLazyPagingItems()

        PharmacistScreen {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Name: ${medicine.name}", modifier = Modifier
                        .weight(0.1f)
                )

                Text(
                    text = "Description: ${medicine.description}", Modifier
                        .weight(0.1f)
                )

                MeteredAsyncImage(
                    url = medicine.boxPhotoUrl,
                    contentDescription = "Box Photo",
                    modifier = Modifier
                        .weight(0.1f)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(0.1f)
                ) {
                    items(pharmacies.itemCount) { index ->
                        val pharmacy = pharmacies[index]!!
                        Box(modifier = Modifier.clickable {
                            onPharmacyClick(pharmacy)
                        }) {
                            Text(text = pharmacy.name)

                        }
                    }
                }
            }
        }
    } else {
        Box {
            LoadingSpinner()
        }
    }
}

