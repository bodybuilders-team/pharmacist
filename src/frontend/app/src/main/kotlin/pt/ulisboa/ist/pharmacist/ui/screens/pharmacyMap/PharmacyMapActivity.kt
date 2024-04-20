package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.os.Bundle
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyActivity

/**
 * Activity for the [PharmacyMapScreen].
 */
class PharmacyMapActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::PharmacyMapViewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getCurrentLocation(this)
        viewModel.checkForLocationAccessPermission(this)

        setContent {
            viewModel.loadPharmacyMap()

            viewModel.state
            PharmacyMapScreen(
                hasLocationPermission = viewModel.hasLocationPermission,
                mapProperties = viewModel.mapProperties,
                cameraPositionState = viewModel.cameraPositionState,
                pharmacies = viewModel.pharmacies,
                onPharmacyDetailsClick = { pid ->
                    PharmacyActivity.navigate(this, pid)
                },
            )
        }
    }


}
