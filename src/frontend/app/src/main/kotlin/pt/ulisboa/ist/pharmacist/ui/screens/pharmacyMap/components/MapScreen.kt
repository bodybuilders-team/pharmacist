package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.LocationOn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconButton

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
    setPosition: (LatLng) -> Unit
) {
    var addingPharmacy by remember { mutableStateOf(false) }
    var newPharmacyMarkerState by remember { mutableStateOf<MarkerState?>(null) }
    var newPharmacyName by remember { mutableStateOf("") }
    var newPharmacyDescription by remember { mutableStateOf("") }

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
                        Text(
                            marker.snippet ?: "No description",
                            style = MaterialTheme.typography.bodySmall
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

            if (addingPharmacy && newPharmacyMarkerState != null) {
                Box(
                    modifier = Modifier
                        .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                        .align(alignment = Alignment.CenterHorizontally)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    Column {
                        IconButton(
                            onClick = {
                                setPosition(newPharmacyMarkerState!!.position)
                            },
                            imageVector = Icons.Rounded.LocationOn,
                            text = "Go to location",
                            contentDescription = "Go to location"
                        )
                        TextField(
                            value = newPharmacyName,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            //fontWeight = FontWeight.Bold,
                            onValueChange = { newPharmacyName = it },
                            label = { Text("Pharmacy Name") },
                            placeholder = { Text("New Pharmacy") }
                        )
                        TextField(
                            value = newPharmacyDescription,
                            textStyle = MaterialTheme.typography.bodySmall,
                            onValueChange = { newPharmacyDescription = it },
                            label = { Text("Pharmacy Description") },
                            placeholder = { Text("No description") }
                        )
                        IconButton(
                            onClick = {
                                // Intent to select or take picture
                                // getPicture()
                            },
                            imageVector = Icons.Rounded.CameraAlt,
                            text = "Select or Take a picture",
                            contentDescription = "Select or Take a picture"
                        )
                        Button(
                            onClick = {
                                // viewModel.addPharmacy(newPharmacyName, newPharmacyDescription, newPharmacyMarkerState!!.position)

                                addingPharmacy = false
                                newPharmacyMarkerState = null
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
    }
}