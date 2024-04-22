package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.services.LocationService
import pt.ulisboa.ist.pharmacist.service.services.hasLocationPermission
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicinePagingSource

/**
 * View model for the [AddMedicineToPharmacyActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 */
class AddMedicineToPharmacyViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager,
    pharmacyId: Long
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var selectedMedicine by mutableStateOf<Medicine?>(null)

    var hasLocationPermission by mutableStateOf(false)
        private set
    private var queryFlow = MutableStateFlow("")
    private val locationFlow = MutableStateFlow<Location?>(null)

    private val _medicinesState = combine(queryFlow, locationFlow) { searchValue, location ->
        Pair(searchValue, location)
    }.flatMapLatest { (search, location) ->
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MedicinePagingSource(
                    pharmacistService.medicinesService,
                    search,
                    pageSize = PAGE_SIZE,
                    location
                )
            },
        ).flow.cachedIn(viewModelScope)
    }

    val medicinesState get() = _medicinesState

    fun searchMedicines(query: String) {
        this.queryFlow.value = query
    }


    fun addMedicineToPharmacy(medicineId: Long, stock: Long) {
        Log.d("AddMedicineToPharmacyViewModel", "addMedicineToPharmacy: $medicineId, $stock")
    }

    fun addMedicine(medicineId: Long) {
        Log.d("AddMedicineToPharmacyViewModel", "addMedicine: $medicineId")
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
        const val PAGE_SIZE = 10
        const val PREFETCH_DISTANCE = 1
    }
}
