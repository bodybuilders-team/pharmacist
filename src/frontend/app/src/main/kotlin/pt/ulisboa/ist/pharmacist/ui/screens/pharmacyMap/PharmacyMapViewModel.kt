package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.LocationService
import pt.ulisboa.ist.pharmacist.service.services.hasLocationPermission
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel

/**
 * View model for the [PharmacyMapActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 * @property state the current state of the view model
 */
class PharmacyMapViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager,
) : PharmacistViewModel(pharmacistService, sessionManager) {
    private var location by mutableStateOf<LatLng?>(null)

    var state: PharmacyMapState by mutableStateOf(PharmacyMapState.UNLOADED)
        private set

    var hasLocationPermission by mutableStateOf(false)
        private set

    var pharmacies by mutableStateOf<List<Pharmacy>>(emptyList())
        private set

    var cameraPositionState by mutableStateOf(CameraPositionState())
        private set

    var followMyLocation by mutableStateOf(true)

    val mapProperties by mutableStateOf(
        MapProperties(
            isMyLocationEnabled = true,
        )
    )

    /**
     * Loads the list of pharmacies.
     */
    fun loadPharmacyList() = viewModelScope.launch {
        if (state != PharmacyMapState.UNLOADED)
            return@launch

        val result = pharmacistService.pharmaciesService.getPharmacies()

        if (result.isSuccess())
            pharmacies = result.data.pharmacies

        state = PharmacyMapState.LOADED
    }

    /**
     * Gets the current location of the user.
     *
     * @param context the context of the app
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context) {
        val locationService = LocationService(context)

        locationService.requestLocationUpdates()
            .collect { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                Log.d("PharmacyMapViewModel", "Location: $latLng")
                if (followMyLocation) {
                    try {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(latLng, DEFAULT_ZOOM)
                            )
                        )
                    } catch (e: CancellationException) {
                        Log.d("PharmacyMapViewModel", "Camera animation cancelled")
                        throw e
                    } catch (e: Exception) {
                        Log.d("PharmacyMapViewModel", "Camera animation failed")
                    }
                }
            }
    }

    /**
     * Checks if the app has location access permission.
     *
     * @param context the context of the app
     */
    fun checkForLocationAccessPermission(context: Context) {
        hasLocationPermission = context.hasLocationPermission()
    }

    enum class PharmacyMapState {
        UNLOADED,
        LOADED
    }

    companion object {
        private const val DEFAULT_ZOOM = 36f
    }
}
