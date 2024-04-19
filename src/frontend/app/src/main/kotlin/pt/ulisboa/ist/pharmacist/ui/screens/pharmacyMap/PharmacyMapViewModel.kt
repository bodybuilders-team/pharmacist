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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.PharmacistService
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
    private var location by mutableStateOf(LatLng(0.0, 0.0))

    var state: PharmacyMapState by mutableStateOf(PharmacyMapState.UNLOADED)
        private set

    var hasLocationPermission by mutableStateOf(false)
        private set

    var pharmacies by mutableStateOf<List<Pharmacy>>(emptyList()) // TODO: Change to pharmacies
        private set

    var cameraPositionState by mutableStateOf(
        CameraPositionState(
            CameraPosition.fromLatLngZoom(
                location,
                12f
            )
        )
    )
        private set

    val mapProperties by mutableStateOf(MapProperties())

    /**
     * Loads the pharmacy home page.
     */
    fun loadPharmacyMap() {
        if (state != PharmacyMapState.UNLOADED)
            return

        // TODO: GET PHARMACIES
        pharmacies = listOf(
            Pharmacy(1, "Farmácia A", LatLng(37.0, -122.0), "picture1"),
            Pharmacy(2, "Farmácia B", LatLng(40.0, -120.0), "picture1")
        )

        state = PharmacyMapState.LOADED
    }

    /**
     * Check if the app has the necessary permissions to access the user's location.
     *
     * @param context the context of the app
     */
    fun checkForLocationAccessPermission(context: Context) {
        hasLocationPermission =
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
                    location = LatLng(latitude, longitude)
                    Log.d("MAP-LOCATION", currlocation.toString())
                    cameraPositionState =
                        CameraPositionState(CameraPosition.fromLatLngZoom(location, 12f))
                }
            }
            .addOnFailureListener { exception: Exception ->
                // Handle failure to get location
                Log.d("MAP-EXCEPTION", exception.message.toString())
            }
    }

    enum class PharmacyMapState {
        UNLOADED,
        LOADED
    }

}
