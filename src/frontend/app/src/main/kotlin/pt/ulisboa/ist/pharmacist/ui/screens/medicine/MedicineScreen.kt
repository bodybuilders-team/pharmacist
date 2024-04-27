package pt.ulisboa.ist.pharmacist.ui.screens.medicine

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.components.MedicinePharmacyEntry
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage


/**
 * Medicine screen.
 *
 * @param medicine the medicine to display
 * @param loadingState the loading state of the medicine
 * @param pharmaciesState the pharmacies that have the medicine
 * @param onPharmacyClick the action to perform when a pharmacy is clicked
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
                    contentDescription = stringResource(R.string.medicine_boxPhoto_description),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(top = 16.dp)
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
                ) {
                    Icon(
                        Icons.Rounded.Notifications, // TODO: Icons.Rounded.NotificationsActive
                        contentDescription = stringResource(R.string.medicine_addToNotifications_button_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = stringResource(
                        R.string.medicine_availableInXPharmacies_text,
                        pharmacies.itemCount
                    ),
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
    } else
        Box {
            LoadingSpinner()
        }
}

