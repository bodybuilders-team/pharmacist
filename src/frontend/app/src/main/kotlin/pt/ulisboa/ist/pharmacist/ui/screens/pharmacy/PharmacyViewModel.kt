package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.changeMedicineStock.MedicineStockOperation
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines.MedicineStockModel
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateSubscription
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyViewModel.ModificationEvent.StockModificationEvent
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
    private val realTimeUpdatesService: RealTimeUpdatesService,
    pharmacyId: Long
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    var pharmacy by mutableStateOf<PharmacyWithUserDataModel?>(null)
        private set

    private val modificationEvents = MutableStateFlow<List<ModificationEvent>>(emptyList())

    private val _medicinesState = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PharmacyMedicinesPagingSource(
                pharmaciesService = pharmacistService.pharmaciesService,
                pid = pharmacyId
            )
        }
    ).flow.cachedIn(viewModelScope)
        .combine(modificationEvents) { pagingData, modificationEvents ->
            modificationEvents.fold(pagingData) { pagingDataAcc, modificationEvent ->
                applyModificationEvent(pagingDataAcc, modificationEvent)
            }
        }

    private fun applyModificationEvent(
        pagingData: PagingData<MedicineStockModel>,
        modificationEvent: ModificationEvent
    ): PagingData<MedicineStockModel> = when (modificationEvent) {
        is StockModificationEvent -> {
            pagingData.map {
                if (it.medicine.medicineId != modificationEvent.medicineId)
                    return@map it

                it.copy(
                    stock = it.stock + when (modificationEvent.operation) {
                        MedicineStockOperation.ADD -> modificationEvent.quantity
                        MedicineStockOperation.REMOVE -> -modificationEvent.quantity
                    }
                )
            }
        }
    }

    val medicinesState get() = _medicinesState

    fun listenForRealTimeUpdates() = viewModelScope.launch {
        realTimeUpdatesService.listenForRealTimeUpdates(
            onPharmacyUserRating = { pharmacyUserRatingData ->
                pharmacy = pharmacy?.copy(userRating = pharmacyUserRatingData.userRating)
            },
            onPharmacyGlobalRating = { pharmacyGlobalRatingData ->
                pharmacy = pharmacy?.let {
                    it.copy(
                        pharmacy = it.pharmacy.copy(
                            globalRating = pharmacyGlobalRatingData.globalRating,
                            numberOfRatings = pharmacyGlobalRatingData.numberOfRatings.toTypedArray()
                        )
                    )
                }
            },
            onPharmacyUserFlagged = { pharmacyUserFlaggedData ->
                pharmacy = pharmacy?.copy(userFlagged = pharmacyUserFlaggedData.flagged)
            },
            onPharmacyUserFavorited = { pharmacyUserFavoritedData ->
                pharmacy =
                    pharmacy?.copy(userMarkedAsFavorite = pharmacyUserFavoritedData.favorited)
            },
        )
    }

    /**
     * Loads the pharmacy with the given [pharmacyId].
     *
     * @param pharmacyId the pharmacy id
     */
    fun loadPharmacy(pharmacyId: Long) = viewModelScope.launch {
        loadingState = LOADING

        val result = pharmacistService.pharmaciesService.getPharmacyById(pharmacyId)
        if (result.isSuccess()) {
            pharmacy = result.data
            realTimeUpdatesService.subscribeToUpdates(
                listOf(
                    RealTimeUpdateSubscription.pharmacyUserRating(pharmacyId),
                    RealTimeUpdateSubscription.pharmacyGlobalRating(pharmacyId),
                    RealTimeUpdateSubscription.pharmacyUserFlagged(pharmacyId),
                    RealTimeUpdateSubscription.pharmacyUserFavorited(pharmacyId)
                )
            )
        }

        loadingState = LOADED
    }

    /**
     * Updates the favorite status of the pharmacy.
     *
     * If the pharmacy is marked as favorite, it will be removed from the user's favorites.
     * If the pharmacy is not marked as favorite, it will be added to the user's favorites.
     */
    fun updateFavoriteStatus() {
        pharmacy?.let {
            viewModelScope.launch {
                if (it.userMarkedAsFavorite) {
                    val result =
                        pharmacistService.usersService.removeFavorite(it.pharmacy.pharmacyId)

                    if (result.isSuccess())
                        pharmacy = pharmacy?.copy(userMarkedAsFavorite = false)
                } else {
                    val result = pharmacistService.usersService.addFavorite(it.pharmacy.pharmacyId)

                    if (result.isSuccess())
                        pharmacy = pharmacy?.copy(userMarkedAsFavorite = true)
                }
            }
        }
    }

    fun updateRating(rating: Int) = pharmacy?.let {
        viewModelScope.launch {
            val result =
                pharmacistService.pharmaciesService.ratePharmacy(it.pharmacy.pharmacyId, rating)

            if (result.isSuccess()) {
                val result2 =
                    pharmacistService.pharmaciesService.getPharmacyById(it.pharmacy.pharmacyId)
                if (result2.isSuccess())
                    pharmacy = result2.data
            }
        }
    }

    fun updateReportStatus() = pharmacy?.let {
        viewModelScope.launch {
            if (it.userFlagged) {
                val result = pharmacistService.usersService.unflagPharmacy(it.pharmacy.pharmacyId)

                if (result.isSuccess())
                    pharmacy = pharmacy?.copy(userFlagged = false)
            } else {
                val result = pharmacistService.usersService.flagPharmacy(it.pharmacy.pharmacyId)

                if (result.isSuccess())
                    pharmacy = pharmacy?.copy(userFlagged = true)
            }
        }
    }

    fun modifyStock(
        medicineId: Long,
        operation: MedicineStockOperation,
        quantity: Long = 1
    ) = pharmacy?.let {
        viewModelScope.launch {
            val result = pharmacistService.pharmaciesService.changeMedicineStock(
                pharmacyId = it.pharmacy.pharmacyId,
                medicineId = medicineId,
                operation = operation,
                stock = quantity
            )

            if (result.isSuccess()) {
                modificationEvents.value += StockModificationEvent(
                    medicineId,
                    operation,
                    quantity
                )
            }
        }
    }

    fun onMedicineAdded(medicineId: Long, quantity: Long) {
        // TODO: This does not work. This paging is sus
        pharmacy?.let {
            modificationEvents.value += StockModificationEvent(
                medicineId,
                MedicineStockOperation.ADD,
                quantity
            )
        }
    }


    enum class PharmacyLoadingState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val PREFETCH_DISTANCE = 1
    }


    sealed class ModificationEvent {
        data class StockModificationEvent(
            val medicineId: Long,
            val operation: MedicineStockOperation,
            val quantity: Long
        ) : ModificationEvent()
    }
}

