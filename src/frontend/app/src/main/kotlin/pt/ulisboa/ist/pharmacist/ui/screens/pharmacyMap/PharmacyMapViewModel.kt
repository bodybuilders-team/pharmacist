package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapViewModel.PharmacyMapNavigationState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapViewModel.PharmacyMapNavigationState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapViewModel.PharmacyMapNavigationState.NOT_LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event

/**
 * View model for the [PharmacyMapActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 * @property loadingState the current loading state of the view model
 * @property state the current state of the view model
 */
class PharmacyMapViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {

    private var _loadingState by mutableStateOf(NOT_LOADING)
    private var _state: PharmacyMapState by mutableStateOf(PharmacyMapState.IDLE)

    private var _hasLocationPermission by mutableStateOf(false)
    private var _location by mutableStateOf(LatLng(0.0, 0.0))
    private var _pharmacies by mutableStateOf<List<Pharmacy>>(emptyList()) // TODO: Change to pharmacies
    private val _mapProperties by mutableStateOf(MapProperties())
    private var _cameraPositionState by mutableStateOf(
        CameraPositionState(
            CameraPosition.fromLatLngZoom(
                location,
                12f
            )
        )
    )

    val hasLocationPermission: Boolean
        get() = _hasLocationPermission

    val pharmacies: List<Pharmacy>
        get() = _pharmacies

    val mapProperties: MapProperties
        get() = _mapProperties

    val location: LatLng
        get() = _location

    val cameraPositionState: CameraPositionState
        get() = _cameraPositionState

    val loadingState: PharmacyMapNavigationState
        get() = _loadingState

    val state
        get() = _state

    /**
     * Loads the pharmacy home page.
     */
    fun loadPharmacyMap() {
        check(state == PharmacyMapState.IDLE) { "The view model is not in the idle state." }

        _state = PharmacyMapState.LOADING_PHARMACY_MAP

        // TODO: GET PHARMACIES
        _pharmacies = listOf(
            Pharmacy("1", "Farmácia A", LatLng(37.0, -122.0), "picture1"),
            Pharmacy("2", "Farmácia B", LatLng(40.0, -120.0), "picture1")
        )

        _state = PharmacyMapState.PHARMACY_MAP_LOADED
    }

    fun navigateToPharmacyDetails(pharmacyId: String) {
        _loadingState = LOADING

        viewModelScope.launch {
            while (state !in listOf(PharmacyMapState.PHARMACY_MAP_LOADED))
                yield()

            //_events.emit(PharmacyMapEvent.Navigate(PharmacyActivity::class.java))
        }
    }

    /**
     * Navigates to the given activity.
     *
     * @param clazz the activity class to navigate to
     */
    fun <T> navigateTo(clazz: Class<T>) {
        _loadingState = LOADING

        viewModelScope.launch {
            while (state !in listOf(PharmacyMapState.PHARMACY_MAP_LOADED))
                yield()

            _events.emit(PharmacyMapEvent.Navigate(clazz))
        }
    }

    /**
     * Gets the current location of the user.
     *
     * @param context the context of the app
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { currlocation: Location? ->
                if (currlocation != null) {
                    val latitude = currlocation.latitude
                    val longitude = currlocation.longitude
                    _location = LatLng(latitude, longitude)
                    Log.d("MAP-LOCATION", currlocation.toString())
                    _cameraPositionState =
                        CameraPositionState(CameraPosition.fromLatLngZoom(location, 12f))
                }
            }
            .addOnFailureListener { exception: Exception ->
                // Handle failure to get location
                Log.d("MAP-EXCEPTION", exception.message.toString())
            }
    }

    /**
     * Check if the app has the necessary permissions to access the user's location.
     *
     * @param context the context of the app
     */
    fun checkForLocationAccessPermission(context: Context) {
        _hasLocationPermission =
            !(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Navigates to the given activity.
     *
     * @param T the type of the activity to navigate to
     */
    inline fun <reified T> navigateTo() {
        navigateTo(T::class.java)
    }

    /**
     * Sets the loading state to [LOADED].
     */
    fun setLoadingStateToLoaded() {
        _loadingState = LOADED
    }


    enum class PharmacyMapState {
        IDLE,
        LOADING_PHARMACY_MAP,
        PHARMACY_MAP_LOADED
    }

    /**
     * The loading state of the [PharmacyMapViewModel] regarding navigation events.
     *
     * @property NOT_LOADING the home screen is idle
     * @property LOADING the home screen is loading
     * @property LOADED the home screen is not loading
     */
    enum class PharmacyMapNavigationState {
        NOT_LOADING,
        LOADING,
        LOADED
    }

    /**
     * The events of the [PharmacyMapViewModel].
     */
    sealed class PharmacyMapEvent : Event {

        /**
         * A navigation event.
         *
         * @property clazz the activity class to navigate to
         */
        class Navigate(val clazz: Class<*>) : PharmacyMapEvent()
    }
}
