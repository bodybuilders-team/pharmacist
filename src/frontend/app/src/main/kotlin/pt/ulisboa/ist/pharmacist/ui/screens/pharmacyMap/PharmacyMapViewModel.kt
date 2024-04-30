package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.LocationService
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils

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

    var pharmacyPhotoUrl by mutableStateOf<String?>(null)
    var newPharmacyPhoto by mutableStateOf<ImageBitmap?>(null)
    var state: PharmacyMapState by mutableStateOf(PharmacyMapState.UNLOADED)
        private set

    var hasLocationPermission by mutableStateOf(false)
    var hasCameraPermission by mutableStateOf(false)

    var pharmacies by mutableStateOf<List<Pharmacy>>(emptyList())
        private set

    var cameraPositionState by mutableStateOf(CameraPositionState())
        private set

    var followMyLocation by mutableStateOf(false)

    val mapProperties by mutableStateOf(
        MapProperties(
            isMyLocationEnabled = true,
        )
    )

    fun uploadBoxPhoto(boxPhotoData: ByteArray, mediaType: MediaType) = viewModelScope.launch {
        ImageHandlingUtils.uploadBoxPhoto(boxPhotoData, mediaType, pharmacistService)
            ?.let {
                pharmacyPhotoUrl = it.boxPhotoUrl
                newPharmacyPhoto = it.boxPhoto
            }
    }

    /**
     * Loads the list of pharmacies.
     */
    fun loadPharmacyList() = viewModelScope.launch {
        if (state == PharmacyMapState.LOADING) return@launch

        state = PharmacyMapState.LOADING

        val result = pharmacistService.pharmaciesService.getPharmacies(limit = 1000)

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
                Log.d("PharmacyMapViewModel_CurrentLocation", "Location: $latLng")
                if (followMyLocation) {
                    try {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(latLng, DEFAULT_ZOOM)
                            )
                        )
                    } catch (e: CancellationException) {
                        Log.d("PharmacyMapViewModel_CurrentLocation", "Camera animation cancelled")
                        throw e
                    } catch (e: Exception) {
                        Log.d("PharmacyMapViewModel_CurrentLocation", "Camera animation failed")
                    }
                }
            }
    }

    fun setPosition(latLng: LatLng) = viewModelScope.launch {
        followMyLocation = false
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(latLng, cameraPositionState.position.zoom)
            )
        )
    }

    fun addPharmacy(name: String, location: Location) {
        if (pharmacyPhotoUrl == null) {
            Log.e("AddPharmacy", "Box photo URL is null")
            return
        }
        if (name == "") {
            Log.e("AddPharmacy", "Name and description must not be empty")
            return
        }
        viewModelScope.launch {
            val result = pharmacistService.pharmaciesService.addPharmacy(
                name = name,
                pharmacyPhotoUrl = pharmacyPhotoUrl!!,
                location = location
            )

            if (result.isSuccess()) {
                loadPharmacyList()
                pharmacyPhotoUrl = null
                newPharmacyPhoto = null
            }
        }
    }

    private var job: Job? = null
    lateinit var placesClient: PlacesClient
    lateinit var geoCoder: Geocoder
    val locationAutofill = mutableListOf<AutocompleteResult>()

    fun searchPlaces(query: String) {
        job?.cancel()
        job = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()

            placesClient
                .findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    locationAutofill.clear()
                    locationAutofill.addAll(response.autocompletePredictions.map {
                        AutocompleteResult(
                            it.getFullText(null).toString(),
                            it.placeId
                        )
                    })
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    println(it.cause)
                    println(it.message)
                }
        }
    }


    fun onPlaceClick(placeId: String) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    val latLng = it.place.latLng
                    if (latLng != null) {
                        setPosition(latLng)
                        locationAutofill.clear()
                    }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }


    enum class PharmacyMapState {
        UNLOADED,
        LOADING,
        LOADED
    }

    data class AutocompleteResult(
        val address: String,
        val placeId: String
    )

    companion object {
        private const val DEFAULT_ZOOM = 15f
    }
}
