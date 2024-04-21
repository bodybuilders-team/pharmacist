package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyActivity

/**
 * Activity for the [PharmacyMapScreen].
 */
class PharmacyMapActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::PharmacyMapViewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getCurrentLocation(this@PharmacyMapActivity)
        }

        viewModel.checkForLocationAccessPermission(this)

        setContent {
            viewModel.loadPharmacyList()

            PharmacyMapScreen(
                followMyLocation = viewModel.followMyLocation,
                hasLocationPermission = viewModel.hasLocationPermission,
                mapProperties = viewModel.mapProperties,
                cameraPositionState = viewModel.cameraPositionState,
                pharmacies = viewModel.pharmacies,
                onPharmacyDetailsClick = { pid ->
                    PharmacyActivity.navigate(this, pid)
                },
                setFollowMyLocation = { followMyLocation ->
                    viewModel.followMyLocation = followMyLocation
                },
                setPosition = { location -> viewModel.setPosition(location) }
            )
        }
    }


}
