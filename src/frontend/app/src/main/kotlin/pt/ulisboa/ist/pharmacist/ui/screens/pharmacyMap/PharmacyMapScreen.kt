package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.LocationPermissionScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.MapScreen

/**
 * Pharmacy screen.
 *
 * @param hasLocationPermission true if the app has the necessary permissions, false otherwise
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 * @param pharmacies list of pharmacies to display
 * @param mapProperties properties of the map
 * @param  state of the camera position
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 */
@Composable
fun PharmacyMapScreen(
    hasLocationPermission: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: List<Pharmacy>,
    onPharmacyDetailsClick: (Long) -> Unit,
    toggleFollowMyLocation : () -> Unit
) {
    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            var hasPermission by remember {
                mutableStateOf(hasLocationPermission)
            }

            if (hasPermission)
                MapScreen(
                    mapProperties = mapProperties,
                    cameraPositionState = cameraPositionState,
                    onPharmacyDetailsClick = onPharmacyDetailsClick,
                    pharmacies = pharmacies,
                    toggleFollowMyLocation = toggleFollowMyLocation
                )
            else
                LocationPermissionScreen(onPermissionGranted = { hasPermission = true })
        }
    }
}

