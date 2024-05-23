package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicine
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.repository.network.connection.isFailure
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineRemoteMediator
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
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
@HiltViewModel(assistedFactory = AddMedicineToPharmacyViewModel.Factory::class)
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

    var medicinePagingFlow: Flow<PagingData<MedicineWithClosestPharmacy>>? by mutableStateOf(null)


    fun searchMedicines(query: String) {
        this.queryFlow.value = query
    }

    suspend fun addMedicineToPharmacy(medicineId: Long, stock: Long): Boolean {
        if (stock <= 0) {
            Log.e("AddMedicineToPharmacyViewModel", "Invalid stock")
            return false
        }

        Log.d("AddMedicineToPharmacyViewModel", "addMedicineToPharmacy: $medicineId, $stock")

        val result = try {
            pharmacyApi.addNewMedicineToPharmacy(
                pharmacyId,
                medicineId,
                stock
            )
        } catch (e: Exception) {
            Log.e("AddMedicineToPharmacyViewModel", "Failed to add medicine to pharmacy", e)
            return false
        }

        if (result.isFailure()) {
            Log.d("AddMedicineToPharmacyViewModel", "Failed to add medicine to pharmacy")
            return false
        }

        Log.d("AddMedicineToPharmacyViewModel", "Medicine added to pharmacy")

        return true
    }

    fun addMedicine(medicineId: Long) = viewModelScope.launch {
        Log.d("AddMedicineToPharmacyViewModel", "addMedicine: $medicineId")

        val result = try {
            medicineApi.getMedicineById(medicineId)
        } catch (e: Exception) {
            Log.e("AddMedicineToPharmacyViewModel", "Failed to get medicine from API", e)
            return@launch
        }
        if (result.isFailure()) {
            Log.d("AddMedicineToPharmacyViewModel", "Failed to get medicine from API")
            return@launch
        }

        pharmacistDb.medicineDao().upsertMedicine(result.data.toMedicineEntity())
        val medicine = pharmacistDb.medicineDao().getMedicineById(medicineId).toMedicine()
        selectedMedicine = MedicineWithClosestPharmacy(
            medicine.medicineId,
            medicine.name,
            medicine.description,
            medicine.boxPhotoUrl,
            null
        )
    }


    fun checkForLocationAccessPermission(context: Context) {
        hasLocationPermission = context.hasLocationPermission()
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    fun obtainLocation(context: Context) {
        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    Log.d("MedicineSearchViewModel", "Location: $it")
                    val location = Location(it.latitude, it.longitude)

                    medicinePagingFlow = queryFlow.flatMapLatest { query ->
                        Pager(
                            config = PagingConfig(
                                pageSize = PAGE_SIZE,
                                prefetchDistance = PREFETCH_DISTANCE,
                                enablePlaceholders = false,
                                initialLoadSize = PAGE_SIZE
                            ),
                            remoteMediator = MedicineRemoteMediator(
                                pharmacistDb = pharmacistDb,
                                medicineApi = medicineApi,
                                query = query,
                                location = location
                            ),
                            pagingSourceFactory = { pharmacistDb.medicineDao().pagingSource() }
                        )
                            .flow
                            .map { pagingData ->
                                pagingData.map {
                                    it.toMedicineWithClosestPharmacy()
                                }
                            }
                    }
                        .cachedIn(viewModelScope)
                }
            }
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val PREFETCH_DISTANCE = 1
    }

    enum class AddMedicineToPharmacyState {
        NOT_LOADED,
        LOADING,
        LOADED
    }
}
