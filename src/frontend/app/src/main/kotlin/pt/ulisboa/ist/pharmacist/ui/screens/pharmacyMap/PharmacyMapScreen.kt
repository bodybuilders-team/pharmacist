package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.util.Log
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
 * @param loadingState the current state of the loading operation
 */
@Composable
fun PharmacyMapScreen(
    hasLocationPermission: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: List<Pharmacy>,
    onPharmacyDetailsClick: (String) -> Unit,
    loadingState: PharmacyMapViewModel.PharmacyMapNavigationState
) {

    Log.d("Pharmacy 2", pharmacies.size.toString())
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
                    loadingMap = loadingState == PharmacyMapViewModel.PharmacyMapNavigationState.LOADING,
                    mapProperties = mapProperties,
                    cameraPositionState = cameraPositionState,
                    onPharmacyDetailsClick = onPharmacyDetailsClick,
                    pharmacies = pharmacies
                )
            else
                LocationPermissionScreen(onPermissionGranted = { hasPermission = true })
        }
    }
}

@Preview
@Composable
private fun PharmacyMapScreenPreview() {
    PharmacyMapScreen(
        onPharmacyDetailsClick = {},
        hasLocationPermission = true,
        pharmacies = listOf(),
        mapProperties = MapProperties(),
        cameraPositionState = rememberCameraPositionState(),
        loadingState = PharmacyMapViewModel.PharmacyMapNavigationState.LOADED
    )
}