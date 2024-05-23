package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
 * @param followMyLocation if the map should follow the user's location.
 * @param hasLocationPermission if the user has location permission.
 * @param hasCameraPermission if the user has camera permission.
 * @param mapProperties the map properties.
 * @param cameraPositionState the camera position state.
 * @param pharmacies the pharmacies.
 * @param onPharmacyDetailsClick the callback to be called when a pharmacy is clicked.
 * @param onAddPictureButtonClick the callback to be called when the add picture button is clicked.
 * @param onAddPharmacyFinishClick the callback to be called when the add pharmacy finish button is clicked.
 * @param onAddPharmacyCancelClick the callback to be called when the add pharmacy cancel button is clicked.
 * @param newPharmacyPhoto the new pharmacy photo.
 * @param setFollowMyLocation the callback to be called when the follow my location button is clicked.
 * @param setPosition the callback to be called when the position is set.
 * @param locationAutofill the location autofill.
 * @param onSearchPlaces the callback to be called when the search places button is clicked.
 * @param onPlaceClick the callback to be called when a place is clicked.
 */
@Composable
fun PharmacyMapScreen(
    followMyLocation: Boolean,
    zoomedInMyLocation: Boolean,
    hasLocationPermission: Boolean,
    hasCameraPermission: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: SnapshotStateMap<Long, Pharmacy>,
    onPharmacyDetailsClick: (Long) -> Unit,
    onAddPictureButtonClick: () -> Unit,
    onAddPharmacyFinishClick: (newPharmacyName: String, location: Location) -> Unit,
    onAddPharmacyCancelClick: () -> Unit,
    newPharmacyPhoto: ImageBitmap?,
    setFollowMyLocation: (Boolean) -> Unit,
    setPosition: (LatLng) -> Unit,
    locationAutofill: MutableList<PharmacyMapViewModel.AutocompleteResult>,
    onSearchPlaces: (String) -> Unit,
    onPlaceClick: (PharmacyMapViewModel.AutocompleteResult) -> Unit,
    searchQuery: String,
    userSuspended: Boolean
) {
    PharmacistScreen {
        var rememberedHasLocationPermission by remember { mutableStateOf(hasLocationPermission) }

        if (rememberedHasLocationPermission)
            MapScreen(
                hasCameraPermission = hasCameraPermission,
                followMyLocation = followMyLocation,
                zoomedInMyLocation = zoomedInMyLocation,
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
                onPlaceClick = onPlaceClick,
                searchQuery = searchQuery,
                userSuspended = userSuspended
            )
        else
            PermissionScreen(
                onPermissionGranted = {
                    rememberedHasLocationPermission = true
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

