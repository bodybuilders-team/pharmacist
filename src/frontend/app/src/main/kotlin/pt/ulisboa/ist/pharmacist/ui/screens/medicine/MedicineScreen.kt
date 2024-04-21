package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
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

                MeteredAsyncImage(
                    url = medicine.boxPhotoUrl,
                    contentDescription = "Box Photo",
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                )

                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(8.dp),
                )

                Text(
                    text = medicine.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier

                )

                IconButton(
                    modifier = Modifier,
                    onClick = { /*TODO*/ },
                ) { // TODO: Implement notifications and change button based on the state (if notifications are enabled, then
                    // the button should be filled, otherwise it should be outlined, or something like that)
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Add to notifications",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Available in ${pharmacies.itemCount} pharmacies",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(0.1f)
                        .padding(bottom = 8.dp)
                ) {

                    items(pharmacies.itemCount) { index ->
                        val pharmacy = pharmacies[index]!!
                        Box(modifier = Modifier
                            .clickable {
                                onPharmacyClick(pharmacy)
                            }) {
                            Text(text = pharmacy.name)
                        }
                    }
                }
            }
        }
    } else
        Box {
            LoadingSpinner()
        }
}

