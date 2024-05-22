package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineRemoteMediator
import pt.ulisboa.ist.pharmacist.service.LocationService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasLocationPermission
import javax.inject.Inject

/**
 * View model for the [MedicineSearchActivity].
 *
 * @property pharmacistDb the local database
 * @property medicineApi the remote medicine API
 * @property sessionManager the manager used to handle the user session
 *
 */
@HiltViewModel
class MedicineSearchViewModel @Inject constructor(
    private val pharmacistDb: PharmacistDatabase,
    private val medicineApi: MedicineApi,
    sessionManager: SessionManager
) : PharmacistViewModel(sessionManager) {
    var hasLocationPermission by mutableStateOf(false)
        private set
    private var queryFlow = MutableStateFlow("")
    private val locationFlow = MutableStateFlow<Location?>(null)

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val medicinePagingFlow = combine(queryFlow, locationFlow) { query, location ->
        Log.d("MedicineRemoteMediator", "Newflow - Query: $query, Location: $location")
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

    /*private val _medicinesState = combine(queryFlow, locationFlow) { searchValue, location ->
        Pair(searchValue, location)
    }.flatMapLatest { (search, location) ->
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MedicinePagingSource(
                    medicinesService = pharmacistRepository.medicinesRepository,
                    query = search,
                    location = location
                )
            },
        ).flow
    }*/

    // val medicinesState get() = _medicinesState

    fun searchMedicines(query: String) {
        this.queryFlow.value = query
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
    }
}
