package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import dagger.hilt.android.AndroidEntryPoint
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasCameraPermission
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasLocationPermission

/**
 * Activity for the [PharmacyMapScreen].
 */
@AndroidEntryPoint
class PharmacyMapActivity : PharmacistActivity() {

    private val viewModel: PharmacyMapViewModel by viewModels()

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            if (result.data?.extras?.get("data") is Bitmap)
                handleTakePhoto(result)
            else
                handleImageSelection(result)
        }

    private fun handleImageSelection(result: ActivityResult) {
        ImageHandlingUtils.handleImageSelection(contentResolver, result)
            ?.let { (inputStream, mediaType) ->
                viewModel.uploadBoxPhoto(inputStream.readBytes(), mediaType)
            }
    }

    private fun handleTakePhoto(result: ActivityResult) {
        ImageHandlingUtils.handleTakePhoto(result)
            ?.let { (boxPhotoData, mediaType) ->
                viewModel.uploadBoxPhoto(boxPhotoData, mediaType)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.hasLocationPermission = hasLocationPermission()
        viewModel.hasCameraPermission = hasCameraPermission()
        viewModel.listenForRealTimeUpdates()
        viewModel.loadPharmacyList()

        setContent {
            LaunchedEffect(key1 = viewModel.hasLocationPermission) {
                if (viewModel.hasLocationPermission)
                    viewModel.getCurrentLocation(this@PharmacyMapActivity)
            }

            PharmacyMapScreen(
                followMyLocation = viewModel.followMyLocation,
                zoomedInMyLocation = viewModel.zoomedInMyLocation,
                hasLocationPermission = viewModel.hasLocationPermission,
                hasCameraPermission = viewModel.hasCameraPermission,
                mapProperties = viewModel.mapProperties,
                pharmacies = viewModel.pharmacies,
                cameraPositionState = viewModel.cameraPositionState,
                onPharmacyDetailsClick = { pid ->
                    PharmacyActivity.navigate(this, pid)
                },
                onAddPictureButtonClick = {
                    imageResultLauncher.launch(ImageHandlingUtils.getChooserIntent())
                },
                onAddPharmacyFinishClick = { name, location ->
                    viewModel.addPharmacy(
                        name = name,
                        location = location
                    )
                },
                onAddPharmacyCancelClick = {
                    viewModel.pharmacyPhotoUrl = null
                    viewModel.newPharmacyPhoto = null
                },
                newPharmacyPhoto = viewModel.newPharmacyPhoto,
                setFollowMyLocation = { followMyLocation ->
                    viewModel.followMyLocation = followMyLocation
                },
                setPosition = { location -> viewModel.setPosition(location) },
                locationAutofill = viewModel.locationAutofill,
                onSearchPlaces = { query -> viewModel.searchPlaces(query) },
                onPlaceClick = { placeId -> viewModel.onPlaceClick(placeId) },
                searchQuery = viewModel.searchQuery,
                userSuspended = viewModel.userSuspended
            )
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.hasLocationPermission = hasLocationPermission()
        viewModel.hasCameraPermission = hasCameraPermission()
        viewModel.loadPharmacyList()
    }
}
