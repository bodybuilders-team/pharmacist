package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyViewModel.PharmacyLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyViewModel.PharmacyLoadingState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyViewModel.PharmacyLoadingState.NOT_LOADED

/**
 * View model for the [PharmacyActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 * @property loadingState the current loading state of the view model
 */
class PharmacyViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager,
    pharmacyId: Long
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    var pharmacy by mutableStateOf<PharmacyWithUserDataModel?>(null)
        private set

    private val _medicinesState = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE
        ),
        pagingSourceFactory = {
            PharmacyMedicinesPagingSource(
                pharmaciesService = pharmacistService.pharmaciesService,
                pageSize = PAGE_SIZE,
                pid = pharmacyId
            )
        }
    ).flow.cachedIn(viewModelScope)

    val medicinesState get() = _medicinesState

    /**
     * Loads the pharmacy with the given [pharmacyId].
     *
     * @param pharmacyId the pharmacy id
     */
    fun loadPharmacy(pharmacyId: Long) = viewModelScope.launch {
        loadingState = LOADING

        val result = pharmacistService.pharmaciesService.getPharmacyById(pharmacyId)
        if (result.isSuccess())
            pharmacy = result.data

        loadingState = LOADED
    }

    /**
     * Updates the favorite status of the pharmacy.
     *
     * If the pharmacy is marked as favorite, it will be removed from the user's favorites.
     * If the pharmacy is not marked as favorite, it will be added to the user's favorites.
     */
    fun updateFavoriteStatus() { // TODO: Test if successfull
        pharmacy?.let {
            viewModelScope.launch {
                pharmacy = if (it.userMarkedAsFavorite) {
                    pharmacistService.usersService.removeFavorite(it.pharmacy.pharmacyId)
                    pharmacy?.copy(userMarkedAsFavorite = false)
                } else {
                    pharmacistService.usersService.addFavorite(it.pharmacy.pharmacyId)
                    pharmacy?.copy(userMarkedAsFavorite = true)
                }
            }
        }
    }


    enum class PharmacyLoadingState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    companion object {
        const val PAGE_SIZE = 10
        const val PREFETCH_DISTANCE = 1
    }
}
