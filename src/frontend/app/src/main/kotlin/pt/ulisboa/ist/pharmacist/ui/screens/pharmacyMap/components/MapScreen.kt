package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner

/**
 * Screen to display the map
 *
 * @param loadingMap true if the map is loading, false otherwise
 * @param mapProperties the properties of the map
 * @param pharmacies the list of markers to display on the map
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 */
@Composable
fun MapScreen(
    loadingMap: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: List<Pharmacy>,
    onPharmacyDetailsClick: (String) -> Unit
) {
    if (loadingMap)
        Box {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                LoadingSpinner()
            }
        }
    else
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
        ) {
            pharmacies.forEach {pharmacy ->
                MarkerInfoWindowContent(
                    state = MarkerState(position = pharmacy.location),
                    title = pharmacy.name,
                    onInfoWindowClick = { onPharmacyDetailsClick(pharmacy.id) }
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
        }
}