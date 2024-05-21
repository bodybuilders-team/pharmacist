package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.repository.network.connection.isFailure
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineRemoteMediator
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import pt.ulisboa.ist.pharmacist.service.LocationService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy.AddMedicineToPharmacyViewModel.AddMedicineToPharmacyState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy.AddMedicineToPharmacyViewModel.AddMedicineToPharmacyState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy.AddMedicineToPharmacyViewModel.AddMedicineToPharmacyState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasLocationPermission

/**
 * View model for the [AddMedicineToPharmacyActivity].
 *
 * @property sessionManager the manager used to handle the user session
 * @property pharmacyId the id of the pharmacy
 * @property selectedMedicine the selected medicine
 * @property hasLocationPermission true if the user has location permission, false otherwise
 */
@HiltViewModel
class AddMedicineToPharmacyViewModel @AssistedInject constructor(
    private val pharmacistDb: PharmacistDatabase,
    private val pharmacyApi: PharmacyApi,
    private val medicineApi: MedicineApi,
    sessionManager: SessionManager,
    @Assisted val pharmacyId: Long
) : PharmacistViewModel(sessionManager) {

    @AssistedFactory
    interface Factory {
        fun create(pharmacyId: Long): AddMedicineToPharmacyViewModel
    }

    var selectedMedicine by mutableStateOf<MedicineWithClosestPharmacy?>(null)

    var hasLocationPermission by mutableStateOf(false)
        private set
    private var queryFlow = MutableStateFlow("")
    private val locationFlow = MutableStateFlow<Location?>(null)

    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    val medicinePagingFlow = combine(queryFlow, locationFlow) { query, location ->
        Pair(query, location)
    }.flatMapLatest { (query, location) ->
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            remoteMediator = MedicineRemoteMediator(
                pharmacistDb = pharmacistDb,
                medicineApi = medicineApi,
                query,
                location
            ),
            pagingSourceFactory = {
                pharmacistDb.medicineDao().pagingSourceMedicineNotInPharmacy(query, pharmacyId)
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map {
                    it.toMedicineWithClosestPharmacy()
                }
            }
    }
        .cachedIn(viewModelScope)

    fun loadAvailableMedicines(pharmacyId: Long) = viewModelScope.launch {
        loadingState = LOADING

        val result = pharmacyApi.listAllAvailableMedicines(pharmacyId)

        if (result.isFailure()) {
            Log.e("AddMedicineToPharmacyViewModel", "Failed to list all available medicines")
            loadingState = NOT_LOADED
            return@launch
        }

        if (result.isSuccess()) {
            Log.d(
                "AddMedicineToPharmacyViewModel",
                "Available medicines loaded, ${result.data.medicines}"
            )
            val availableMedicines = result.data.medicines
        }

        loadingState = LOADED
    }


    fun searchMedicines(query: String) {
        this.queryFlow.value = query
    }


    suspend fun addMedicineToPharmacy(medicineId: Long, stock: Long): Boolean {
        if (stock <= 0) {
            Log.e("AddMedicineToPharmacyViewModel", "Invalid stock")
            return false
        }

        Log.d("AddMedicineToPharmacyViewModel", "addMedicineToPharmacy: $medicineId, $stock")

        val result = pharmacyApi.addNewMedicineToPharmacy(
            pharmacyId,
            medicineId,
            stock
        )

        if (result.isFailure()) {
            Log.e("AddMedicineToPharmacyViewModel", "Failed to add medicine to pharmacy")
            return false
        }

        Log.d("AddMedicineToPharmacyViewModel", "Medicine added to pharmacy")

        return true
    }

    fun addMedicine(medicineId: Long) = viewModelScope.launch {
        Log.d("AddMedicineToPharmacyViewModel", "addMedicine: $medicineId")

        val result = medicineApi.getMedicineById(medicineId)

        if (result.isFailure()) {
            Log.e("AddMedicineToPharmacyViewModel", "Failed to get medicine with id $medicineId")
            return@launch
        }

        val medicine = result.data
        selectedMedicine = MedicineWithClosestPharmacy(
            medicine.medicine.medicineId,
            medicine.medicine.name,
            medicine.medicine.description,
            medicine.medicine.boxPhotoUrl,
            null
        )
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


    companion object {
        private const val PAGE_SIZE = 10
        private const val PREFETCH_DISTANCE = 1

        fun provideFactory(
            assistedFactory: Factory,
            pharmacyId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(pharmacyId) as T
            }
        }
    }

    enum class AddMedicineToPharmacyState {
        NOT_LOADED,
        LOADING,
        LOADED
    }
}

@InstallIn(PharmacistActivity::class)
@Module
interface AssistedInjectModule