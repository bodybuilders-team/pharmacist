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
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.LocationService
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils

/**
 * View model for the [PharmacyMapActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 * @property placesClient the client used to interact with the Places API
 * @property geoCoder the geocoder used to get the location of an address
 *
 * @property state the current state of the view model
 */
class PharmacyMapViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager,
    val placesClient: PlacesClient,
    val geoCoder: Geocoder
) : PharmacistViewModel(pharmacistService, sessionManager) {

    var pharmacyPhotoUrl by mutableStateOf<String?>(null)
    var newPharmacyPhoto by mutableStateOf<ImageBitmap?>(null)
    var state: PharmacyMapState by mutableStateOf(PharmacyMapState.UNLOADED)
        private set

    var hasLocationPermission by mutableStateOf(false)
    var hasCameraPermission by mutableStateOf(false)

    var pharmacies by mutableStateOf<List<PharmacyWithUserDataModel>>(emptyList())
        private set

    var cameraPositionState by mutableStateOf(CameraPositionState())
        private set

    var followMyLocation by mutableStateOf(true)

    val mapProperties by mutableStateOf(
        MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL
        )
    )

    private var searchQueryJob: Job? = null
    val locationAutofill = mutableListOf<AutocompleteResult>()
    var searchQuery by mutableStateOf("")
        private set

    /**
     * Uploads the box photo to the server.
     *
     * @param boxPhotoData the data of the box photo
     * @param mediaType the media type of the box photo
     */
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

    /**
     * Sets the position of the camera and the search query.
     *
     * @param latLng the latitude and longitude of the position
     */
    fun setPosition(latLng: LatLng) = viewModelScope.launch {
        followMyLocation = false
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(latLng, cameraPositionState.position.zoom)
            )
        )

        val addresses = geoCoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1
        )

        if (addresses?.isNotEmpty() == true)
            searchQuery = addresses[0].getAddressLine(0)
    }

    /**
     * Adds a pharmacy to the server.
     *
     * @param name the name of the pharmacy
     * @param location the location of the pharmacy
     */
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


    /**
     * Searches for places based on a query and updates the list of autofill results.
     *
     * @param query the query to search for
     */
    fun searchPlaces(query: String) {
        searchQuery = query
        searchQueryJob?.cancel()
        searchQueryJob = viewModelScope.launch {
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


    /**
     * Handles the click on a place in the autofill list.
     *
     * @param place the place that was clicked
     */
    fun onPlaceClick(place: AutocompleteResult) {
        searchQuery = place.address
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(place.placeId, placeFields)
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

    /**
     * An autocomplete result.
     *
     * @property address the address of the result
     * @property placeId the ID of the place

     */
    data class AutocompleteResult(
        val address: String,
        val placeId: String
    )

    companion object {
        private const val DEFAULT_ZOOM = 14f
    }
}
