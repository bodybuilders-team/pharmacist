package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.content.Context
import android.net.ConnectivityManager
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
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.connection.isFailure
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

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
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
                "PharmacyViewModel",
                "fetchAllMedicines - Fetching medicines for pharmacy ${pharmacy!!.pharmacyId}" +
                        " with offset $offset and limit $limit"
            )
            val result = try {
                pharmacyApi.listAvailableMedicines(
                    pharmacyId = pharmacyId,
                    limit = limit,
                    offset = offset
                )
            } catch (e: Exception) {
                Log.e("PharmacyViewModel", "Failed to fetch medicines from API", e)
                null
            }

            if (result == null || result.isFailure()) {
                Log.d("PharmacyViewModel", "Medicines not retrieved from API")
            } else if (result.isSuccess()) {
                result as APIResult.Success
                Log.d("PharmacyViewModel", "Medicines loaded from API: ${result.data.medicines}")

                pharmacistDb.medicineDao().upsertPharmacyMedicineList(
                    result.data.medicines.map {
                        it.toPharmacyMedicineEntity(pharmacyId)
                    }
                )
            }
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

            Log.d("PharmacyViewModel", "Got medicines from database: ${medicinesList.size}")

            realTimeUpdatesService.subscribeToUpdates(
                pharmacistDb.medicineDao().getPharmacyMedicineByPharmacyId(pharmacyId)
                    .map {
                        RealTimeUpdateSubscription.pharmacyMedicineStock(
                            pharmacyId = pharmacyId,
                            medicineId = it.medicineId
                        )
                    }
            )

            if (result == null || result.isFailure() ||
                (result as APIResult.Success).data.medicines.isEmpty()
            ) break
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

        reloadPharmacy()

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
    }

    private suspend fun reloadPharmacy() {
        val result = try {
            pharmacyApi.getPharmacyById(pharmacyId)
        } catch (e: Exception) {
            Log.e("PharmacyViewModel", "Failed to load pharmacy from API", e)
            null
        }
        if (result != null && result.isSuccess()) {
            result as APIResult.Success
            pharmacistDb.pharmacyDao().upsertPharmacy(result.data.toPharmacyEntity())
            Log.d("PharmacyViewModel", "Loaded pharmacy from API")
        }

        pharmacy = pharmacistDb.pharmacyDao().getPharmacyById(pharmacyId).toPharmacy()
        Log.d("PharmacyViewModel", "Got pharmacy from database: $pharmacy")
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
                val result = if (it.userMarkedAsFavorite) {
                    try {
                        usersApi.removeFavorite(it.pharmacyId)
                    } catch (e: Exception) {
                        Log.e("PharmacyViewModel", "Failed to remove favorite pharmacy in API", e)
                        null
                    }
                } else {
                    try {
                        usersApi.addFavorite(it.pharmacyId)
                    } catch (e: Exception) {
                        Log.e("PharmacyViewModel", "Failed to add favorite pharmacy in API", e)
                        null
                    }
                }

                if (result != null && result.isSuccess()) {
                    reloadPharmacy()
                }
            }
        }
    }

    fun updateRating(rating: Int) =
        viewModelScope.launch {
            val result = try {
                pharmacyApi.ratePharmacy(pharmacyId, rating)
            } catch (e: Exception) {
                Log.e("PharmacyViewModel", "Failed to update pharmacy rating in API", e)
                null
            }

            if (result != null && result.isSuccess()) {
                reloadPharmacy()
            }
        }

    suspend fun updateReportStatus(): Boolean {
        Log.d("PharmacyViewModel", "Flagging pharmacy $pharmacyId")
        val result = try {
            usersApi.flagPharmacy(pharmacyId)
        } catch (e: Exception) {
            Log.e("PharmacyViewModel", "Failed to flag pharmacy in API", e)
            null
        }
        if (result != null && result.isSuccess()) {
            reloadPharmacy()
            Log.d("PharmacyViewModel", "Pharmacy $pharmacyId flagged")
            return true
        }

        return false
    }

    fun modifyStock(
        medicineId: Long,
        operation: MedicineStockOperation,
        quantity: Long = 1
    ) = pharmacy?.let {
        viewModelScope.launch {
            val result = try {
                pharmacyApi.changeMedicineStock(
                    pharmacyId = it.pharmacyId,
                    medicineId = medicineId,
                    operation = operation,
                    stock = quantity
                )
            } catch (e: Exception) {
                Log.e("PharmacyViewModel", "Failed to modify stock in API", e)
                null
            }
            if (result != null && result.isSuccess()) {
                Log.d("PharmacyViewModel", "Modified stock in API")
            }
        }
    }

    fun onMedicineAdded(medicineId: Long, quantity: Long) {
        viewModelScope.launch {
            val result = try {
                medicineApi.getMedicineById(medicineId)
            } catch (e: Exception) {
                Log.e("PharmacyViewModel", "Failed to get medicine from API", e)
                null
            }

            if (result != null && result.isSuccess()) {
                result as APIResult.Success
                pharmacistDb.withTransaction {
                    pharmacistDb.medicineDao().upsertMedicine(result.data.toMedicineEntity())

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

