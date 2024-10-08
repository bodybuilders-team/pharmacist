package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacy
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import pt.ulisboa.ist.pharmacist.repository.remote.upload.UploaderApi
import pt.ulisboa.ist.pharmacist.service.LocationService
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateSubscription
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils
import javax.inject.Inject

/**
 * View model for the [PharmacyMapActivity].
 *
 * @property sessionManager the manager used to handle the user session
 * @property placesClient the client used to interact with the Places API
 * @property geoCoder the geocoder used to get the location of an address
 *
 * @property state the current state of the view model
 */
@HiltViewModel
class PharmacyMapViewModel @Inject constructor(
    private val pharmacistDb: PharmacistDatabase,
    private val pharmacyApi: PharmacyApi,
    private val uploaderApi: UploaderApi,
    private val realTimeUpdatesService: RealTimeUpdatesService,
    private val placesClient: PlacesClient,
    private val geoCoder: Geocoder,
    sessionManager: SessionManager
) : PharmacistViewModel(sessionManager) {

    var pharmacyPhotoUrl by mutableStateOf<String?>(null)
    var newPharmacyPhoto by mutableStateOf<ImageBitmap?>(null)
    var state: PharmacyMapState by mutableStateOf(PharmacyMapState.UNLOADED)
        private set

    var hasLocationPermission by mutableStateOf(false)
    var hasCameraPermission by mutableStateOf(false)

    val pharmacies = mutableStateMapOf<Long, Pharmacy>()

    var cameraPositionState by mutableStateOf(CameraPositionState())
        private set

    var followMyLocation by mutableStateOf(true)
    var zoomedInMyLocation by mutableStateOf(false)

    val userSuspended by mutableStateOf(sessionManager.isSuspended)

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

    fun listenForRealTimeUpdates() = viewModelScope.launch {
        realTimeUpdatesService.listenForRealTimeUpdates(
            onNewPharmacy = { newPharmacy ->
                pharmacies[newPharmacy.pharmacyId] =
                    Pharmacy(
                        pharmacyId = newPharmacy.pharmacyId,
                        name = newPharmacy.name,
                        location = newPharmacy.location,
                        pictureUrl = newPharmacy.pictureUrl,
                        globalRating = null,
                        numberOfRatings = emptyArray(),
                        userRating = null,
                        userMarkedAsFavorite = false,
                        userFlagged = false
                    )
            },
            onPharmacyUserRating = { pharmacyUserRatingData ->
                pharmacies.compute(pharmacyUserRatingData.pharmacyId) { _, pharmacy ->
                    pharmacy?.copy(userRating = pharmacyUserRatingData.userRating)
                }
            },
            onPharmacyGlobalRating = { pharmacyGlobalRatingData ->
                pharmacies.compute(pharmacyGlobalRatingData.pharmacyId) { _, pharmacy ->
                    pharmacy?.copy(
                        globalRating = pharmacyGlobalRatingData.globalRating,
                        numberOfRatings = pharmacyGlobalRatingData.numberOfRatings.toTypedArray()
                    )
                }
            },
            onPharmacyUserFlagged = { pharmacyUserFlaggedData ->
                pharmacies.compute(pharmacyUserFlaggedData.pharmacyId) { _, pharmacyWithUserData ->
                    pharmacyWithUserData?.copy(userFlagged = pharmacyUserFlaggedData.flagged)
                }
            },
            onPharmacyUserFavorited = { pharmacyUserFavoritedData ->
                pharmacies.compute(pharmacyUserFavoritedData.pharmacyId) { _, pharmacyWithUserData ->
                    pharmacyWithUserData?.copy(userMarkedAsFavorite = pharmacyUserFavoritedData.favorited)
                }
            }
        )
    }

    /**
     * Uploads the box photo to the server.
     *
     * @param boxPhotoData the data of the box photo
     * @param mediaType the media type of the box photo
     */
    fun uploadBoxPhoto(boxPhotoData: ByteArray, mediaType: MediaType) = viewModelScope.launch {
        ImageHandlingUtils.uploadBoxPhoto(boxPhotoData, mediaType, uploaderApi)
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

        val result = try {
            pharmacyApi.getPharmacies(limit = 1000)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get pharmacies", e)
            null
        }

        if (result != null && result.isSuccess()) {
            result as APIResult.Success

            pharmacistDb.pharmacyDao()
                .upsertPharmacies(result.data.pharmacies.map { it.toPharmacyEntity() })
        }
        pharmacistDb.pharmacyDao().getAllPharmacies().forEach {
            pharmacies[it.pharmacyId] = it.toPharmacy()
        }
        realTimeUpdatesService.subscribeToUpdates(
            listOf(RealTimeUpdateSubscription.newPharmacies()) +
                    pharmacistDb.pharmacyDao().getAllPharmacies().flatMap {
                        listOf(
                            RealTimeUpdateSubscription.pharmacyUserRating(it.pharmacyId),
                            RealTimeUpdateSubscription.pharmacyGlobalRating(it.pharmacyId),
                            RealTimeUpdateSubscription.pharmacyUserFlagged(it.pharmacyId),
                            RealTimeUpdateSubscription.pharmacyUserFavorited(it.pharmacyId)
                        )
                    }
        )

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
                Log.d(TAG, "Location: $latLng, followMyLocation: $followMyLocation")
                if (followMyLocation || !zoomedInMyLocation) {
                    while (true) {
                        try {
                            Log.d(TAG, "Animating camera")
                            cameraPositionState.animate(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.fromLatLngZoom(latLng, DEFAULT_ZOOM)
                                )
                            )
                            Log.d(TAG, "Camera animation done")
                            if (!zoomedInMyLocation)
                                zoomedInMyLocation = true
                            break
                        } catch (e: CancellationException) {
                            Log.d(
                                TAG,
                                "Camera animation cancelled: followMyLocation: $followMyLocation, zoomedInMyLocation: $zoomedInMyLocation"
                            )
                            throw e
                        } catch (e: Exception) {
                            Log.d(
                                TAG,
                                "Camera animation failed: followMyLocation: $followMyLocation, zoomedInMyLocation: $zoomedInMyLocation"
                            )
                        }
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
        Log.d(TAG, "Setting position to $latLng")
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
            Log.e(TAG, "Box photo URL is null")
            return
        }
        if (name == "") {
            Log.e(TAG, "Name and description must not be empty")
            return
        }
        viewModelScope.launch {
            val result = try {
                pharmacyApi.addPharmacy(
                    name = name,
                    pharmacyPhotoUrl = pharmacyPhotoUrl!!,
                    location = location
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add pharmacy", e)
                return@launch
            }

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
        private const val DEFAULT_ZOOM = 13f
        private const val TAG = "PharmacyMapViewModel"
    }
}
