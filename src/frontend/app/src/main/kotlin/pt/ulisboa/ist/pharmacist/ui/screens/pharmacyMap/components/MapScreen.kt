package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
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
import androidx.compose.ui.res.stringResource
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
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapViewModel

/**
 * Screen to display the map
 *
 * @param hasCameraPermission true if the app has the necessary permissions, false otherwise
 * @param followMyLocation true if the map should follow the user's location, false otherwise
 * @param mapProperties properties of the map
 * @param cameraPositionState of the camera position
 * @param pharmacies list of pharmacies to display
 * @param onPharmacyDetailsClick callback to be invoked when the user clicks on the pharmacy details button
 * @param onAddPictureButtonClick callback to be invoked when the user clicks on the add picture button
 * @param onAddPharmacyFinishClick callback to be invoked when the user clicks on the add pharmacy finish button
 * @param onAddPharmacyCancelClick callback to be invoked when the user clicks on the add pharmacy cancel button
 * @param newPharmacyPhoto photo of the new pharmacy
 * @param setFollowMyLocation callback to be invoked when the user clicks on the follow my location button
 * @param setPosition callback to be invoked when the position is to be changed
 * @param locationAutofill list of locations to autofill
 * @param onSearchPlaces callback to be invoked when the user searches for places
 * @param onPlaceClick callback to be invoked when the user clicks on a place
 * @param searchQuery the query to search for
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    hasCameraPermission: Boolean,
    followMyLocation: Boolean,
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    pharmacies: List<PharmacyWithUserDataModel>,
    onPharmacyDetailsClick: (Long) -> Unit,
    onAddPictureButtonClick: () -> Unit,
    onAddPharmacyFinishClick: (newPharmacyName: String, location: Location) -> Unit,
    onAddPharmacyCancelClick: () -> Unit,
    newPharmacyPhoto: ImageBitmap?,
    setFollowMyLocation: (Boolean) -> Unit,
    setPosition: (LatLng) -> Unit,
    locationAutofill: List<PharmacyMapViewModel.AutocompleteResult>,
    onSearchPlaces: (String) -> Unit,
    onPlaceClick: (PharmacyMapViewModel.AutocompleteResult) -> Unit,
    searchQuery: String
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var clickedPharmacyMarker by rememberSaveable { mutableStateOf<Long?>(null) }
    var hasCameraPermission_ by remember { mutableStateOf(hasCameraPermission) }

    var addingPharmacy by rememberSaveable { mutableStateOf(false) }
    var pickingOnMap by rememberSaveable { mutableStateOf(false) }
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
        )
            setFollowMyLocation(false) // Stop following location when user moves the map
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving)
            setPosition(cameraPositionState.position.target)
    }

    val scaffoldSheetScope = rememberCoroutineScope()
    val scaffoldSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldSheetState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (clickedPharmacyMarker != null) {
                clickedPharmacyMarker?.let { pharmacyId ->
                    pharmacies.find { p -> p.pharmacy.pharmacyId == pharmacyId }?.let { pharmacy ->
                        PharmacyDetails(onPharmacyDetailsClick, pharmacy.pharmacy)
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
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        scrollGesturesEnabled = !addingPharmacy || pickingOnMap,
                        myLocationButtonEnabled = !addingPharmacy || pickingOnMap
                    ),
                    properties = mapProperties,
                    onMyLocationButtonClick = {
                        setFollowMyLocation(!followMyLocation)
                        false
                    },
                    onMapClick = {
                        scaffoldSheetScope.launch {
                            scaffoldSheetState.bottomSheetState.hide()
                        }
                        clickedPharmacyMarker = null
                    }
                ) {
                    pharmacies.forEach { p ->
                        Marker(
                            state = MarkerState(position = p.pharmacy.location.toLatLng()),
                            onClick = { _ ->
                                if (!addingPharmacy) {
                                    clickedPharmacyMarker = p.pharmacy.pharmacyId
                                    scaffoldSheetScope.launch {
                                        scaffoldSheetState.bottomSheetState.expand()
                                    }
                                }
                                false
                            },
                            onInfoWindowClick = { onPharmacyDetailsClick(p.pharmacy.pharmacyId) },
                            icon = BitmapDescriptorFactory.defaultMarker(
                                when {
                                    clickedPharmacyMarker == p.pharmacy.pharmacyId -> BitmapDescriptorFactory.HUE_RED
                                    p.userMarkedAsFavorite -> BitmapDescriptorFactory.HUE_YELLOW
                                    else -> BitmapDescriptorFactory.HUE_GREEN
                                }
                            )
                        )
                    }

                    if (addingPharmacy)
                        Marker(
                            state = newPharmacyMarkerState
                                ?: MarkerState(cameraPositionState.position.target)
                        )

                }
                if (!addingPharmacy || pickingOnMap)
                    SearchPlacesBar(
                        searchQuery = searchQuery,
                        locationAutofill = locationAutofill,
                        onSearchPlaces = onSearchPlaces,
                        onPlaceClick = onPlaceClick,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
            }

            if (addingPharmacy && !pickingOnMap)
                AddPharmacyWindow(
                    modifier = Modifier.align(if (isLandscape) Alignment.TopStart else Alignment.TopCenter),
                    onPickOnMap = {
                        newPharmacyMarkerState = null
                        pickingOnMap = true
                    },
                    onAddPictureButtonClick = { onAddPictureButtonClick() },
                    onAddPharmacyFinishClick = { newPharmacyName ->
                        newPharmacyMarkerLocation?.let {
                            onAddPharmacyFinishClick(
                                newPharmacyName,
                                Location(it.latitude, it.longitude)
                            )
                            addingPharmacy = false
                            newPharmacyMarkerState = null
                        }
                    },
                    addPharmacyButtonEnabled = newPharmacyMarkerLocation != null,
                    newPharmacyPhoto = newPharmacyPhoto,
                    searchQuery = searchQuery
                )

            AddPharmacyButton(addingPharmacy, pickingOnMap, onClick = {
                if (addingPharmacy) {
                    if (pickingOnMap) {
                        newPharmacyMarkerState = MarkerState(cameraPositionState.position.target)
                        setPosition(cameraPositionState.position.target)
                        pickingOnMap = false
                    } else {
                        addingPharmacy = false
                        newPharmacyMarkerState = null
                        onAddPharmacyCancelClick()
                    }
                } else
                    addingPharmacy = true
            })
        }
    }
}
