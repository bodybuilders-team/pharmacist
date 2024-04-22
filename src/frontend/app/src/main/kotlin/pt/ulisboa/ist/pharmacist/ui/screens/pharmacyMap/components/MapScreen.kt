package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

/**
 * Screen to display the map
 *
 * @param mapProperties the properties of the map
 * @param pharmacies the list of markers to display on the map
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 */
@Composable
fun MapScreen(
    followMyLocation: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: List<Pharmacy>,
    onPharmacyDetailsClick: (Long) -> Unit,
    onAddPharmacyFinishClick: (newPharmacyName: String, newPharmacyDescription: String, location: Location) -> Unit,
    setFollowMyLocation: (Boolean) -> Unit,
    setPosition: (LatLng) -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var addingPharmacy by rememberSaveable { mutableStateOf(false) }
    var newPharmacyMarkerLocation by rememberSaveable { mutableStateOf<LatLng?>(null) }
    var newPharmacyMarkerState by remember {
        mutableStateOf(newPharmacyMarkerLocation?.let { MarkerState(it) })
    }

    LaunchedEffect(key1 = newPharmacyMarkerState?.position) {
        newPharmacyMarkerLocation = newPharmacyMarkerState?.position
    }

    LaunchedEffect(cameraPositionState.cameraMoveStartedReason) {
        if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE ||
            cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.API_ANIMATION
        ) {
            Log.d("MapScreen", "Camera moved by user, disabling followMyLocation")
            setFollowMyLocation(false)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMyLocationButtonClick = {
                setFollowMyLocation(!followMyLocation)
                false
            },
            onMapClick = { clickedLocation ->
                if (addingPharmacy && newPharmacyMarkerState == null) {
                    newPharmacyMarkerState = MarkerState(clickedLocation)
                    setPosition(clickedLocation)
                }
            }
        ) {
            pharmacies.forEach { pharmacy ->
                MarkerInfoWindowContent(
                    state = MarkerState(position = pharmacy.location.toLatLng()),
                    title = pharmacy.name,
                    onInfoWindowClick = { onPharmacyDetailsClick(pharmacy.pharmacyId) }
                ) { marker ->
                    Column {
                        Text(
                            marker.title ?: "Unnamed Pharmacy",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (pharmacy.globalRating != null)
                            Text(
                                text = "Rating: ${
                                    String.format("%.1f", pharmacy.globalRating)
                                } ⭐", style = MaterialTheme.typography.bodySmall
                            )
                        Text(
                            "(click for more details)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            if (addingPharmacy) {
                newPharmacyMarkerState?.let { markerState ->
                    Marker(
                        state = markerState,
                        draggable = true
                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (addingPharmacy) {
                        addingPharmacy = false
                        newPharmacyMarkerState = null
                    } else addingPharmacy = true
                }
            ) {
                Text(if (!addingPharmacy) "Add Pharmacy" else "Cancel")
            }

            if (addingPharmacy) {
                newPharmacyMarkerLocation?.let { markerLocation ->
                    AddPharmacyWindow(
                        modifier = Modifier.align(
                            alignment = if (isLandscape) Alignment.Start
                            else Alignment.CenterHorizontally
                        ),
                        onGoToLocationButtonClick = { setPosition(markerLocation) },
                        onAddPictureButtonClick = { /* TODO */ },
                        onAddPharmacyFinishClick = { newPharmacyName, newPharmacyDescription ->
                            onAddPharmacyFinishClick(
                                newPharmacyName,
                                newPharmacyDescription,
                                Location(markerLocation.latitude, markerLocation.longitude)
                            )
                            addingPharmacy = false
                            newPharmacyMarkerState = null
                        }
                    )
                }
            }
        }
    }
}