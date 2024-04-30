package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.MapScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.PermissionScreen

/**
 * Pharmacy screen.
 *
 * @param hasLocationPermission true if the app has the necessary permissions, false otherwise
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 * @param pharmacies list of pharmacies to display
 * @param mapProperties properties of the map
 * @param cameraPositionState of the camera position
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 * @param setFollowMyLocation callback to be invoked when the user clicks on the follow my location button
 * @param setPosition callback to be invoked when the position is to be changed
 */
@Composable
fun PharmacyMapScreen(
    followMyLocation: Boolean,
    hasLocationPermission: Boolean,
    hasCameraPermission: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: List<Pharmacy>,
    onPharmacyDetailsClick: (Long) -> Unit,
    onAddPictureButtonClick: () -> Unit,
    onAddPharmacyFinishClick: (newPharmacyName: String, location: Location) -> Unit,
    onAddPharmacyCancelClick: () -> Unit,
    newPharmacyPhoto: ImageBitmap?,
    setFollowMyLocation: (Boolean) -> Unit,
    setPosition: (LatLng) -> Unit,
    locationAutofill: MutableList<PharmacyMapViewModel.AutocompleteResult>,
    onSearchPlaces: (String) -> Unit,
    onPlaceClick: (String) -> Unit
) {
    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            var hasLocationPermission_ by remember { mutableStateOf(hasLocationPermission) }

            if (hasLocationPermission_)
                MapScreen(
                    hasCameraPermission = hasCameraPermission,
                    followMyLocation = followMyLocation,
                    mapProperties = mapProperties,
                    cameraPositionState = cameraPositionState,
                    pharmacies = pharmacies,
                    onPharmacyDetailsClick = onPharmacyDetailsClick,
                    onAddPictureButtonClick = onAddPictureButtonClick,
                    onAddPharmacyFinishClick = onAddPharmacyFinishClick,
                    onAddPharmacyCancelClick = onAddPharmacyCancelClick,
                    newPharmacyPhoto = newPharmacyPhoto,
                    setFollowMyLocation = setFollowMyLocation,
                    setPosition = setPosition,
                    locationAutofill = locationAutofill,
                    onSearchPlaces = onSearchPlaces,
                    onPlaceClick = onPlaceClick
                )
            else
                PermissionScreen(
                    onPermissionGranted = {
                        hasLocationPermission_ = true
                    },
                    permissionRequests = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                    permissionTitle = stringResource(R.string.pharmacy_map_location_permission_title),
                    settingsPermissionNote = stringResource(R.string.pharmacyMap_location_permission_note),
                    settingsPermissionNoteButtonText = stringResource(R.string.permission_settings_button)
                )
        }
    }
}

