package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmaciesPagingSource

/**
 * View model for the [MedicineActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 */
class MedicineViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager,
    medicineId: Long
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    var medicine: Medicine? by mutableStateOf(null)
        private set

    private val _pharmaciesState = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE
        ),
        pagingSourceFactory = {
            PharmaciesPagingSource(
                pharmacistService.pharmaciesService,
                medicineId
            )
        },
    ).flow.cachedIn(viewModelScope)

    val pharmaciesState get() = _pharmaciesState

    fun loadMedicine(mid: Long) = viewModelScope.launch {
        loadingState = MedicineLoadingState.LOADING

        val result = pharmacistService.medicinesService.getMedicineById(mid)
        if (result.isSuccess())
            medicine = result.data

        loadingState = LOADED
    }


    enum class MedicineLoadingState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    companion object {
        const val PAGE_SIZE = 10
        const val PREFETCH_DISTANCE = 1
    }
}

