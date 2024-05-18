package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.GetMedicineOutputModel
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.components.MedicineHeader
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.components.MedicinePharmacyList
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.PermissionScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner


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
    toggleMedicineNotification: () -> Unit,
    onShareClick: () -> Unit
) {
    if (loadingState == MedicineViewModel.MedicineLoadingState.LOADED && medicineModel != null) {
        val (medicine, notificationsActive) = medicineModel

        val pharmacies = pharmaciesState.collectAsLazyPagingItems()
        var hasPermission by remember { mutableStateOf(hasLocationPermission) }

        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

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

            if (isLandscape)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    MedicineHeader(
                        medicine,
                        toggleMedicineNotification,
                        notificationsActive,
                        onShareClick
                    )
                    MedicinePharmacyList(pharmacies, onPharmacyClick)
                }
            else
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    MedicineHeader(
                        medicine,
                        toggleMedicineNotification,
                        notificationsActive,
                        onShareClick
                    )
                    MedicinePharmacyList(pharmacies, onPharmacyClick)
                }
        }
    } else
        Box {
            LoadingSpinner()
        }
}

