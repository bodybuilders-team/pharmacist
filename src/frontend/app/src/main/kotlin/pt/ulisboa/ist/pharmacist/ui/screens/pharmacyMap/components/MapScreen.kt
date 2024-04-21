package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
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
    setFollowMyLocation: (Boolean) -> Unit,
) {
    var addingPharmacy by remember { mutableStateOf(false) }
    var chosenAddedPharmacyLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(cameraPositionState.cameraMoveStartedReason) {
        if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
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
                setFollowMyLocation(false)
                if (addingPharmacy && chosenAddedPharmacyLocation == null) {
                    chosenAddedPharmacyLocation = clickedLocation
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
                        Text(
                            marker.snippet ?: "No description",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "(click for more details)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Light
                        )
                        /*IconButton(
                        onClick = { onPharmacyDetailsClick(it.id) },
                        painter = painterResource(R.drawable.ic_round_info_24),
                        contentDescription = stringResource(R.string.pharmacyMap_detailsButton_description),
                        text = stringResource(R.string.pharmacyMap_detailsButton_text),
                        modifier = Modifier.height(32.dp)
                    )*/
                    }
                }
            }

            var newPharmacyName by remember { mutableStateOf("New Pharmacy") }
            var newPharmacyDescription by remember { mutableStateOf("No description") }

            if (addingPharmacy && chosenAddedPharmacyLocation != null) {
                MarkerInfoWindowContent(
                    state = MarkerState(position = chosenAddedPharmacyLocation!!),
                    title = "New Pharmacy",
                    onInfoWindowClick = {}
                ) { marker ->
                    Column {
                        TextField(
                            value = newPharmacyName,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            //fontWeight = FontWeight.Bold,
                            onValueChange = { newPharmacyName = it },
                            label = { Text("Pharmacy Name") }
                        )
                        TextField(
                            value = newPharmacyDescription,
                            textStyle = MaterialTheme.typography.bodySmall,
                            onValueChange = { newPharmacyDescription = it },
                            label = { Text("Pharmacy Name") }
                        )
                        Button(
                            onClick = {
                                // Add new pharmacy
                                addingPharmacy = false
                                chosenAddedPharmacyLocation = null
                                newPharmacyName = "New Pharmacy"
                                newPharmacyDescription = "No description"
                            }
                        ) {
                            Text("Finish")
                        }
                    }
                }
            }
        }
        Column {
            Button(
                onClick = {
                    if (addingPharmacy) {
                        addingPharmacy = false
                        chosenAddedPharmacyLocation = null
                    } else addingPharmacy = true
                }
            ) {
                Text(if (!addingPharmacy) "Add Pharmacy" else "Cancel")
            }
        }
    }
}