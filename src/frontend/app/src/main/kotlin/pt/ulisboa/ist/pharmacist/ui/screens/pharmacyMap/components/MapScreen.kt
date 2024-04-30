package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import android.Manifest
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage

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
    hasCameraPermission: Boolean,
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
    setPosition: (LatLng) -> Unit,
    locationAutofill: List<PharmacyMapViewModel.AutocompleteResult>,
    onSearchPlaces: (String) -> Unit,
    onPlaceClick: (String) -> Unit
) {
    Log.d("MapScreen", "Rendering MapScreen with locationAutofill size ${locationAutofill.size}")
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


    var hasCameraPermission_ by remember { mutableStateOf(hasCameraPermission) }

    BottomSheetScaffold(
        scaffoldState = scaffoldSheetState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (clickedPharmacyMarker != null) {
                clickedPharmacyMarker?.let { pharmacyId ->
                    pharmacies.find { p -> p.pharmacyId == pharmacyId }?.let { pharmacy ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp)
                                .clickable {
                                    onPharmacyDetailsClick(pharmacy.pharmacyId)
                                }
                        ) {
                            Text(
                                text = pharmacy.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (pharmacy.globalRating != null)
                                Text(
                                    text = "${
                                        String.format("%.1f", pharmacy.globalRating)
                                    } ⭐ (${pharmacy.numberOfRatings.sum()})",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            Text(
                                text = stringResource(R.string.pharmacyMap_clickForDetails_text),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Light
                            )
                            MeteredAsyncImage(
                                url = pharmacy.pictureUrl,
                                contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .padding(top = 16.dp, bottom = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (addingPharmacy && !hasCameraPermission_) {
                PermissionScreen(
                    onPermissionGranted = {
                        hasCameraPermission_ = true
                    },
                    permissionRequests = listOf(
                        Manifest.permission.CAMERA
                    ),
                    permissionTitle = stringResource(R.string.pharmacy_map_camera_permission_title),
                    settingsPermissionNote = stringResource(R.string.pharmacyMap_camera_permission_note),
                    settingsPermissionNoteButtonText = stringResource(R.string.permission_settings_button)
                )
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
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
                            icon = when {
                                clickedPharmacyMarker == pharmacy.pharmacyId -> BitmapDescriptorFactory.defaultMarker()
                                // TODO: Different icon for favorite pharmacies
                                else -> BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_GREEN
                                )
                            },
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

                var searchQuery by remember { mutableStateOf("") }
                Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                    TextField(
                        value = searchQuery,
                        label = { Text(stringResource(R.string.pharmacyMap_searchPlaces_text)) },
                        onValueChange = {
                            searchQuery = it
                            onSearchPlaces(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    AnimatedVisibility(
                        visible = locationAutofill.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(0.dp, 600.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp),
                            )
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(locationAutofill.size) {
                                val autofillEntry = locationAutofill[it]
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable {
                                        searchQuery = autofillEntry.address
                                        onPlaceClick(autofillEntry.placeId)
                                    }
                                ) {
                                    Text(autofillEntry.address)
                                }
                            }
                        }
                    }
                }
            }

            if (addingPharmacy) {
                if (newPharmacyMarkerState == null)
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .align(if (isLandscape) Alignment.TopStart else Alignment.Center)
                    ) {
                        Text(
                            text = stringResource(R.string.pharmacyMap_tapOnMap_text),
                            modifier = Modifier.align(Alignment.TopCenter),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                else
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
                        if (!addingPharmacy)
                            stringResource(R.string.pharmacyMap_addPharmacy_button_text)
                        else
                            stringResource(R.string.pharmacyMap_cancel_button_text),
                    )
                },
                text = {
                    Text(
                        if (!addingPharmacy)
                            stringResource(R.string.pharmacyMap_addPharmacy_button_description)
                        else
                            stringResource(R.string.pharmacyMap_cancel_button_description)
                    )
                }
            )
        }
    }
}