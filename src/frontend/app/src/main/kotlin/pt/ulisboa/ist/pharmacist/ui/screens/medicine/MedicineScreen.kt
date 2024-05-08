package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.GetMedicineOutputModel
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.components.MedicinePharmacyEntry
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.PermissionScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage


/**
 * Medicine screen.
 *
 * @param medicineModel the medicine to display
 * @param loadingState the loading state of the medicine
 * @param pharmaciesState the pharmacies that have the medicine
 * @param onPharmacyClick the action to perform when a pharmacy is clicked
 */
@Composable
fun MedicineScreen(
    hasLocationPermission: Boolean,
    medicineModel: GetMedicineOutputModel?,
    loadingState: MedicineViewModel.MedicineLoadingState,
    pharmaciesState: Flow<PagingData<PharmacyWithUserDataModel>>,
    onPharmacyClick: (Pharmacy) -> Unit,
    toggleMedicineNotification: () -> Unit
) {
    if (loadingState == MedicineViewModel.MedicineLoadingState.LOADED && medicineModel != null) {
        val (medicine, notificationsActive) = medicineModel

        val pharmacies = pharmaciesState.collectAsLazyPagingItems()
        var hasPermission by remember {
            mutableStateOf(hasLocationPermission)
        }

        PharmacistScreen {
            if (!hasPermission) {
                PermissionScreen(
                    onPermissionGranted = {
                        hasPermission = true
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
                    onClick = toggleMedicineNotification,
                ) {
                    Icon(
                        if (notificationsActive) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsOff,
                        contentDescription = stringResource(R.string.medicine_addToNotifications_button_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

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
    } else
        Box {
            LoadingSpinner()
        }
}

