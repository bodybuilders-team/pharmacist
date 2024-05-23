package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

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
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineRemoteMediator
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
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
    private var queryFlow = MutableStateFlow("")

    var medicinePagingFlow: Flow<PagingData<MedicineWithClosestPharmacy>>? by mutableStateOf(null)

    fun searchMedicines(query: String) {
        this.queryFlow.value = query
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    fun obtainLocation(context: Context) {
        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { receivedLocation ->
                receivedLocation?.let {
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
                            remoteMediator = //MedicineWithClosestPharmacyRemoteMediator(
                            MedicineRemoteMediator(
                                pharmacistDb = pharmacistDb,
                                medicineApi = medicineApi,
                                query = query,
                                location = location
                            ),
                            pagingSourceFactory = {
                                pharmacistDb.medicineDao().pagingSource()
                                /*pharmacistDb.medicineDao().medicineWithClosestPharmacyPagingSource(
                                    latitude = location.lat,
                                    longitude = location.lon,
                                )*/
                            }
                        )
                            .flow
                            .map { pagingData ->
                                pagingData.map { medicineWithClosestPharmacyEntity ->
                                    medicineWithClosestPharmacyEntity.toMedicineWithClosestPharmacy()
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
}
