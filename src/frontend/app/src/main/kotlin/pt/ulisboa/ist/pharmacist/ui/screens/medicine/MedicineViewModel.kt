package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithNotificationStatus
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineWithNotificationStatus
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacy
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyRemoteMediator
import pt.ulisboa.ist.pharmacist.service.LocationService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasLocationPermission

/**
 * View model for the [MedicineActivity].
 *
 * @property sessionManager the manager used to handle the user session
 */
@HiltViewModel(assistedFactory = MedicineViewModel.Factory::class)
class MedicineViewModel @AssistedInject constructor(
    private val pharmacistDb: PharmacistDatabase,
    private val medicineApi: MedicineApi,
    private val pharmacyApi: PharmacyApi,
    sessionManager: SessionManager,
    @Assisted val medicineId: Long
) : PharmacistViewModel(sessionManager) {

    @AssistedFactory
    interface Factory {
        fun create(medicineId: Long): MedicineViewModel
    }

    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    var medicine: MedicineWithNotificationStatus? by mutableStateOf(null)
        private set

    var hasLocationPermission by mutableStateOf(false)
        private set
    private val locationFlow = MutableStateFlow<Location?>(null)

    var medicineImage: ImageBitmap? by mutableStateOf(null)
        private set

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val pharmacyPagingFlow = locationFlow.flatMapLatest { location ->
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            remoteMediator = PharmacyRemoteMediator(
                pharmacistDb = pharmacistDb,
                pharmacyApi = pharmacyApi,
                medicineId = medicineId,
                location = location
            ),
            pagingSourceFactory = {
                pharmacistDb.pharmacyDao().getPagingSourceByMedicineId(medicineId = medicineId)
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map {
                    it.toPharmacy()
                }
            }
    }
        .cachedIn(viewModelScope)

    /**
     * Loads the medicine with the given [medicineId].
     */
    fun loadMedicine() = viewModelScope.launch {
        loadingState = MedicineLoadingState.LOADING

        reloadMedicine()

        loadingState = LOADED
    }

    fun toggleMedicineNotification() = viewModelScope.launch {
        medicine?.let {
            val result = if (!it.notificationsActive) {
                try {
                    medicineApi.addMedicineNotification(medicineId)
                } catch (e: Exception) {
                    Log.e("MedicineViewModel", "Failed to add medicine notification", e)
                    null
                }
            } else {
                try {
                    medicineApi.removeMedicineNotification(medicineId)
                } catch (e: Exception) {
                    Log.e("MedicineViewModel", "Failed to remove medicine notification", e)
                    null
                }
            }

            if (result != null && result.isSuccess()) {
                reloadMedicine()
            }
        }
    }

    private suspend fun reloadMedicine() {
        val result = try {
            medicineApi.getMedicineById(medicineId)
        } catch (e: Exception) {
            Log.e("MedicineViewModel", "Failed to load medicine from API", e)
            null
        }
        if (result != null && result.isSuccess()) {
            result as APIResult.Success
            pharmacistDb.medicineDao().upsertMedicine(result.data.toMedicineEntity())
            Log.d("MedicineViewModel", "Loaded medicine from API")
        }

        medicine = pharmacistDb.medicineDao().getMedicineById(medicineId)
            .toMedicineWithNotificationStatus()
        Log.d("MedicineViewModel", "Got medicine from database: $medicine")
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

    /**
     * Downloads the pharmacy image.
     */
    suspend fun downloadImage() {
        medicine?.let {
            withContext(Dispatchers.IO) {
                val img: ImageBitmap? = ImageHandlingUtils.downloadImage(it.boxPhotoUrl)
                if (img == null) {
                    Log.e("PharmacyActivity", "Failed to download image")
                    return@withContext
                }
                medicineImage = img
            }
        }
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

