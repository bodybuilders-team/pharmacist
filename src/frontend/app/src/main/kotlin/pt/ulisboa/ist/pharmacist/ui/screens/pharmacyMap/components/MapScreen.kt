package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

/**
 * Screen to display the map
 *
 * @param mapProperties the properties of the map
 * @param pharmacies the list of markers to display on the map
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    followMyLocation: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: List<Pharmacy>,
    onPharmacyDetailsClick: (Long) -> Unit,
    onAddPictureButtonClick: () -> Unit,
    onAddPharmacyFinishClick: (newPharmacyName: String, location: Location) -> Unit,
    onAddPharmacyCancelClick: () -> Unit,
    newPharmacyPhoto: ImageBitmap?,
    setFollowMyLocation: (Boolean) -> Unit,
    setPosition: (LatLng) -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var clickedPharmacyMarker by rememberSaveable { mutableStateOf<Long?>(null) }

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

    val scaffoldSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val scaffoldSheetScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldSheetState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (clickedPharmacyMarker != null) {
                clickedPharmacyMarker?.let { pharmacyId ->
                    pharmacies.find { p -> p.pharmacyId == pharmacyId }?.let { pharmacy ->
                        Column(
                            modifier = Modifier
                                .size(300.dp)
                                .clickable {
                                    onPharmacyDetailsClick(pharmacy.pharmacyId)
                                }
                        ) {
                            Text(
                                pharmacy.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            if (pharmacy.globalRating != null)
                                Text(
                                    text = "Rating: ${
                                        String.format("%.1f", pharmacy.globalRating)
                                    } ⭐",
                                    fontSize = 20.sp,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            Text(
                                "(click for more details)",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
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
                    scaffoldSheetScope.launch {
                        scaffoldSheetState.bottomSheetState.hide()
                    }
                    clickedPharmacyMarker = null
                }
            ) {
                pharmacies.forEach { pharmacy ->
                    Marker(
                        state = MarkerState(position = pharmacy.location.toLatLng()),
                        //title = pharmacy.name,
                        // TODO: Different icon for favorite pharmacies
                        //icon = if (pharmacy.userFavorite) Icons.Rounded.Favorite else null,
                        onClick = { _ ->
                            if (!addingPharmacy) {
                                clickedPharmacyMarker = pharmacy.pharmacyId
                                scaffoldSheetScope.launch {
                                    scaffoldSheetState.bottomSheetState.expand()
                                }
                            }
                            false
                        },
                        onInfoWindowClick = { onPharmacyDetailsClick(pharmacy.pharmacyId) }
                    )
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

            if (addingPharmacy) {
                newPharmacyMarkerLocation?.let { markerLocation ->
                    AddPharmacyWindow(
                        modifier = Modifier.align(if (isLandscape) Alignment.TopStart else Alignment.TopCenter),
                        onGoToLocationButtonClick = { setPosition(markerLocation) },
                        onAddPictureButtonClick = { onAddPictureButtonClick() },
                        onAddPharmacyFinishClick = { newPharmacyName ->
                            onAddPharmacyFinishClick(
                                newPharmacyName,
                                Location(markerLocation.latitude, markerLocation.longitude)
                            )
                            addingPharmacy = false
                            newPharmacyMarkerState = null
                        },
                        newPharmacyPhoto = newPharmacyPhoto
                    )
                }
            }

            ExtendedFloatingActionButton(
                onClick = {
                    if (addingPharmacy) {
                        addingPharmacy = false
                        newPharmacyMarkerState = null
                        onAddPharmacyCancelClick()
                    } else {
                        addingPharmacy = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                icon = {
                    Icon(
                        if (!addingPharmacy) Icons.Rounded.Add else Icons.Rounded.Cancel,
                        if (!addingPharmacy) "Add pharmacy" else "Cancel",
                    )
                },
                text = { Text(if (!addingPharmacy) "Add Pharmacy" else "Cancel") }
            )
        }
    }
}