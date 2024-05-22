package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicine
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacy
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacyMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.network.connection.UnexpectedResponseException
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.MedicineStockOperation
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import pt.ulisboa.ist.pharmacist.repository.remote.users.UsersApi
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateSubscription
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyViewModel.PharmacyLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyViewModel.PharmacyLoadingState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyViewModel.PharmacyLoadingState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils

/**
 * View model for the [PharmacyActivity].
 *
 * @property sessionManager the manager used to handle the user session
 * @property loadingState the current loading state of the view model
 */
@HiltViewModel(assistedFactory = PharmacyViewModel.Factory::class)
class PharmacyViewModel @AssistedInject constructor(
    sessionManager: SessionManager,
    private val pharmacistDb: PharmacistDatabase,
    private val medicineApi: MedicineApi,
    private val pharmacyApi: PharmacyApi,
    private val usersApi: UsersApi,
    private val realTimeUpdatesService: RealTimeUpdatesService,
    @Assisted val pharmacyId: Long
) : PharmacistViewModel(sessionManager) {

    @AssistedFactory
    interface Factory {
        fun create(pharmacyId: Long): PharmacyViewModel
    }

    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    var pharmacy by mutableStateOf<Pharmacy?>(null)
        private set

    var pharmacyImage by mutableStateOf<ImageBitmap?>(null)
        private set

    //private val modificationEvents = MutableSharedFlow<ModificationEvent>()

    //private val newMedicineFlow = MutableStateFlow<Long?>(null)

    val medicinesList =
        mutableStateMapOf<Long, MedicineStock>()

    /*@OptIn(ExperimentalCoroutinesApi::class)
     private val _medicinesState = newMedicineFlow.flatMapLatest {
         Pager(
             config = PagingConfig(
                 pageSize = PAGE_SIZE,
                 prefetchDistance = PREFETCH_DISTANCE,
                 enablePlaceholders = false
             ),
             pagingSourceFactory = {
                 PharmacyMedicinesPagingSource(
                     pharmaciesService = pharmacistService.pharmaciesService,
                     realTimeUpdatesService = realTimeUpdatesService,
                     pid = pharmacyId
                 )
             }
         ).flow.cachedIn(viewModelScope)
             .combine(modificationEvents) { pagingData, modificationEvents ->
                 modificationEvents.fold(pagingData) { pagingDataAcc, modificationEvent ->
                     applyModificationEvent(pagingDataAcc, modificationEvent)
                 }
             }
     }*/

    private fun fetchAllMedicines() = viewModelScope.launch {
        if (pharmacy == null) return@launch

        var offset = 0L
        val limit = 50L
        while (true) {
            Log.d(
                "RealTimeUpdatesService",
                "fetchAllMedicines - Fetching medicines for pharmacy ${pharmacy!!.pharmacyId}" +
                        " with offset $offset and limit $limit"
            )
            val result = pharmacyApi.listAvailableMedicines(
                pharmacyId = pharmacyId,
                limit = limit,
                offset = offset
            )
            if (!result.isSuccess() || result.data.medicines.isEmpty()) break

            pharmacistDb.withTransaction {
                pharmacistDb.medicineDao().upsertPharmacyMedicineList(
                    result.data.medicines.map {
                        it.toPharmacyMedicineEntity(pharmacyId)
                    }
                )
                pharmacistDb.medicineDao().getPharmacyMedicineByPharmacyId(pharmacyId)
                    .forEach { pharmacyMedicine ->
                        medicinesList[pharmacyMedicine.medicineId] = MedicineStock(
                            medicine = Medicine(
                                medicineId = pharmacyMedicine.medicineId,
                                name = pharmacyMedicine.name,
                                description = pharmacyMedicine.description,
                                boxPhotoUrl = pharmacyMedicine.boxPhotoUrl
                            ),
                            stock = pharmacyMedicine.stock ?: 0
                        )
                    }
            }

            realTimeUpdatesService.subscribeToUpdates(
                result.data.medicines.map {
                    RealTimeUpdateSubscription.pharmacyMedicineStock(
                        pharmacyId = pharmacyId,
                        medicineId = it.medicine.medicineId
                    )
                }
            )
            offset += limit
        }
    }

    /*private fun applyModificationEvent(
        pagingData: PagingData<MedicineStockModel>,
        modificationEvent: ModificationEvent
    ): PagingData<MedicineStockModel> = when (modificationEvent) {
        is StockModificationEvent -> {
            Log.d("RealTimeUpdatesService", "Applying stock modification event: $modificationEvent")
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
    }*/

    //val medicinesState get() = _medicinesState

    fun listenForRealTimeUpdates() = viewModelScope.launch {
        realTimeUpdatesService.listenForRealTimeUpdates(
            onPharmacyUserRating = { pharmacyUserRatingData ->
                pharmacy = pharmacy?.copy(userRating = pharmacyUserRatingData.userRating)
            },
            onPharmacyGlobalRating = { pharmacyGlobalRatingData ->
                pharmacy = pharmacy?.copy(
                    globalRating = pharmacyGlobalRatingData.globalRating,
                    numberOfRatings = pharmacyGlobalRatingData.numberOfRatings.toTypedArray()
                )
            },
            onPharmacyUserFlagged = { pharmacyUserFlaggedData ->
                pharmacy = pharmacy?.copy(userFlagged = pharmacyUserFlaggedData.flagged)
            },
            onPharmacyUserFavorited = { pharmacyUserFavoritedData ->
                pharmacy =
                    pharmacy?.copy(userMarkedAsFavorite = pharmacyUserFavoritedData.favorited)
            },
            onMedicineStock = { medicineStockData ->
                Log.d(
                    "RealTimeUpdatesService",
                    "Received medicine stock update: $medicineStockData"
                )
                medicinesList.compute(medicineStockData.medicineId) { _, medicineStock ->
                    medicineStock?.copy(stock = medicineStockData.stock)
                }
            }
        )
    }

    /**
     * Loads the pharmacy with the given [pharmacyId].
     *
     * @param pharmacyId the pharmacy id
     */
    fun loadPharmacy(pharmacyId: Long) = viewModelScope.launch {
        loadingState = LOADING

        val result = try {
            pharmacyApi.getPharmacyById(pharmacyId)
        } catch (e: UnexpectedResponseException) {
            Log.e("PharmacyViewModel", "Failed to load pharmacy", e)
            loadingState = NOT_LOADED
            return@launch
        } 

        if (result.isSuccess()) {
            Log.d("PharmacyViewModel", "Pharmacy loaded: ${result.data}")
            pharmacistDb.withTransaction {
                pharmacistDb.pharmacyDao().upsertPharmacies(listOf(result.data.toPharmacyEntity()))
                pharmacy = pharmacistDb.pharmacyDao().getPharmacyById(pharmacyId).toPharmacy()
            }

            realTimeUpdatesService.subscribeToUpdates(
                listOf(
                    RealTimeUpdateSubscription.pharmacyUserRating(pharmacyId),
                    RealTimeUpdateSubscription.pharmacyGlobalRating(pharmacyId),
                    RealTimeUpdateSubscription.pharmacyUserFlagged(pharmacyId),
                    RealTimeUpdateSubscription.pharmacyUserFavorited(pharmacyId)
                )
            )
            loadingState = LOADED
            fetchAllMedicines()
        } else {
            Log.d("PharmacyViewModel", "Pharmacy not retrieved")
        }


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
                        usersApi.removeFavorite(it.pharmacyId)

                    if (result.isSuccess())
                        pharmacy = pharmacy?.copy(userMarkedAsFavorite = false)
                } else {
                    val result =
                        usersApi.addFavorite(it.pharmacyId)

                    if (result.isSuccess())
                        pharmacy = pharmacy?.copy(userMarkedAsFavorite = true)
                }
            }
        }
    }

    fun updateRating(rating: Int) = pharmacy?.let {
        viewModelScope.launch {
            val result =
                pharmacyApi.ratePharmacy(
                    it.pharmacyId,
                    rating
                )

            if (result.isSuccess()) {
                val result2 =
                    pharmacyApi.getPharmacyById(it.pharmacyId)
                if (result2.isSuccess()) {
                    pharmacistDb.withTransaction {
                        pharmacistDb.pharmacyDao()
                            .upsertPharmacies(listOf(result2.data.toPharmacyEntity()))
                        pharmacy =
                            pharmacistDb.pharmacyDao().getPharmacyById(it.pharmacyId).toPharmacy()
                    }
                }
            }
        }
    }

    suspend fun updateReportStatus(): Boolean {
        pharmacy?.let {
            Log.d("PharmacyViewModel", "Flagging pharmacy ${it.pharmacyId}")
            val result = usersApi.flagPharmacy(it.pharmacyId)
            if (result.isSuccess()) {
                pharmacy = pharmacy?.copy(userFlagged = true)
                Log.d("PharmacyViewModel", "Pharmacy ${it.pharmacyId} flagged")
                return@updateReportStatus true
            }
        }

        return false
    }

    fun modifyStock(
        medicineId: Long,
        operation: MedicineStockOperation,
        quantity: Long = 1
    ) = pharmacy?.let {
        viewModelScope.launch {
            pharmacyApi.changeMedicineStock(
                pharmacyId = it.pharmacyId,
                medicineId = medicineId,
                operation = operation,
                stock = quantity
            )
        }
    }

    fun onMedicineAdded(medicineId: Long, quantity: Long) {
        viewModelScope.launch {
            val result = medicineApi.getMedicineById(medicineId)

            if (result.isSuccess()) {
                pharmacistDb.withTransaction {
                    pharmacistDb.medicineDao()
                        .upsertMedicines(listOf(result.data.toMedicineEntity()))

                    medicinesList[medicineId] =
                        MedicineStock(
                            medicine = pharmacistDb.medicineDao().getMedicineById(medicineId)
                                .toMedicine(),
                            stock = quantity
                        )
                }

                realTimeUpdatesService.subscribeToUpdates(
                    listOf(
                        RealTimeUpdateSubscription.pharmacyMedicineStock(pharmacyId, medicineId)
                    )
                )
            }
        }
    }

    /**
     * Downloads the pharmacy image.
     */
    suspend fun downloadImage() {
        pharmacy?.let {
            withContext(Dispatchers.IO) {
                val img: ImageBitmap? = ImageHandlingUtils.downloadImage(it.pictureUrl)
                if (img == null) {
                    Log.e("PharmacyActivity", "Failed to download image")
                    return@withContext
                }
                pharmacyImage = img
            }
        }
    }

    enum class PharmacyLoadingState {
        NOT_LOADED,
        LOADING,
        LOADED;

        fun isLoaded() = this == LOADED
    }

    sealed class ModificationEvent {
        data class StockModificationEvent(
            val medicineId: Long,
            val operation: MedicineStockOperation,
            val quantity: Long
        ) : ModificationEvent()
    }
}

