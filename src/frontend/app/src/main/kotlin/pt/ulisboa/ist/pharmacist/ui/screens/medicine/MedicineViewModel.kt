package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.medicines.GetMedicineOutputModel
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.LocationService
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasLocationPermission

/**
 * View model for the [MedicineActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 */
class MedicineViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager,
    val medicineId: Long
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    var medicine: GetMedicineOutputModel? by mutableStateOf(null)
        private set

    var hasLocationPermission by mutableStateOf(false)
        private set
    private val locationFlow = MutableStateFlow<Location?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _pharmaciesState = locationFlow.flatMapLatest { location ->
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PharmaciesPagingSource(
                    pharmaciesService = pharmacistService.pharmaciesService,
                    mid = medicineId,
                    location = location
                )
            },
        ).flow.cachedIn(viewModelScope)
    }

    val pharmaciesState get() = _pharmaciesState

    /**
     * Loads the medicine with the given [mid].
     */
    fun loadMedicine(mid: Long) = viewModelScope.launch {
        loadingState = MedicineLoadingState.LOADING

        val result = pharmacistService.medicinesService.getMedicineById(mid)
        if (result.isSuccess())
            medicine = result.data

        loadingState = LOADED
    }

    fun toggleMedicineNotification() = viewModelScope.launch {
        medicine?.let { (_, notificationsActive) ->
            if (!notificationsActive) {
                val result = pharmacistService.medicinesService.addMedicineNotification(medicineId)
                if (result.isSuccess())
                    medicine = medicine?.copy(notificationsActive = true)
            } else {
                val result =
                    pharmacistService.medicinesService.removeMedicineNotification(medicineId)
                if (result.isSuccess())
                    medicine = medicine?.copy(notificationsActive = false)
            }

        }
    }

    fun checkForLocationAccessPermission(context: Context) {
        hasLocationPermission = context.hasLocationPermission()
    }

    suspend fun startObtainingLocation(context: Context) {
        val locationService = LocationService(context)

        locationService.requestLocationUpdates()
            .map {
                locationFlow.emit(Location(it.latitude, it.longitude))
            }
            .collect()
    }

    enum class MedicineLoadingState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val PREFETCH_DISTANCE = 1
    }
}

