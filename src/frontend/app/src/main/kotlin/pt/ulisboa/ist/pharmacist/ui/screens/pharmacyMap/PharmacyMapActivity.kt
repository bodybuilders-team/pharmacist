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

    override fun onResume() {
        super.onResume()
        viewModel.loadPharmacyList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getCurrentLocation(this@PharmacyMapActivity)
        }

        viewModel.checkForLocationAccessPermission(this)

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
                onAddPharmacyFinishClick = { name, description, location ->
                    viewModel.addPharmacy(
                        name = name,
                        description = description,
                        picture = "https://www.indice.eu/img/farmacias/farmacia-estacio-370.jpg",
                        location = location
                    )
                },
                setFollowMyLocation = { followMyLocation ->
                    viewModel.followMyLocation = followMyLocation
                },
                setPosition = { location -> viewModel.setPosition(location) }
            )
        }
    }


}
