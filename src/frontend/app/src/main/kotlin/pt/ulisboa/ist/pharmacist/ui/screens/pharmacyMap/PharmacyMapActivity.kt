package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.services.hasLocationPermission
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils

/**
 * Activity for the [PharmacyMapScreen].
 */
class PharmacyMapActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::PharmacyMapViewModel)

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            if (result.data?.extras?.get("data") is Bitmap) {
                handleTakePhoto(result)
            } else {
                handleImageSelection(result)
            }
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

        lifecycleScope.launch {
            viewModel.getCurrentLocation(this@PharmacyMapActivity)
        }

        viewModel.hasLocationPermission = hasLocationPermission()
        viewModel.loadPharmacyList()

        setContent {
            PharmacyMapScreen(
                followMyLocation = viewModel.followMyLocation,
                hasLocationPermission = viewModel.hasLocationPermission,
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
                setPosition = { location -> viewModel.setPosition(location) }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.hasLocationPermission = hasLocationPermission()
        viewModel.loadPharmacyList()
    }


}
