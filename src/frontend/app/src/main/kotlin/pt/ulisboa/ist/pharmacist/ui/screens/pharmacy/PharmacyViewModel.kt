package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.mappers.toMedicineStock
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacy
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.MedicineStockOperation
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyMedicinesRemoteMediator
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

    private val invalidatingPagingSourceFactory = InvalidatingPagingSourceFactory {
        pharmacistDb.pharmacyDao().pagingSourcePharmacyMedicineByPharmacyId(
            pharmacyId = pharmacyId
        )
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val medicinePagingFlow =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = PharmacyMedicinesRemoteMediator(
                pharmacistDb = pharmacistDb,
                pharmacyApi = pharmacyApi,
                pharmacyId = pharmacyId,
                realTimeUpdatesService = realTimeUpdatesService
            ),
            pagingSourceFactory = invalidatingPagingSourceFactory
        )
            .flow
            .map { pagingData ->
                pagingData.map { pharmacyMedicineFlatEntity ->
                    pharmacyMedicineFlatEntity.toMedicineStock()
                }
            }
            .cachedIn(viewModelScope)

    fun listenForRealTimeUpdates() = viewModelScope.launch {
        realTimeUpdatesService.listenForRealTimeUpdates(
            onPharmacyUserRating = { pharmacyUserRatingData ->
                viewModelScope.launch {
                    pharmacistDb.pharmacyDao().updateUserRating(
                        pharmacyId = pharmacyId,
                        userRating = pharmacyUserRatingData.userRating
                    )
                    pharmacy = pharmacistDb.pharmacyDao().getPharmacyById(pharmacyId).toPharmacy()
                }
            },
            onPharmacyGlobalRating = { pharmacyGlobalRatingData ->
                viewModelScope.launch {
                    pharmacistDb.pharmacyDao().updateGlobalRating(
                        pharmacyId = pharmacyId,
                        globalRating = pharmacyGlobalRatingData.globalRating,
                        numberOfRatings = pharmacyGlobalRatingData.numberOfRatings.toTypedArray()
                    )
                    pharmacy = pharmacistDb.pharmacyDao().getPharmacyById(pharmacyId).toPharmacy()
                }
            },
            onPharmacyUserFlagged = { pharmacyUserFlaggedData ->
                viewModelScope.launch {
                    pharmacistDb.pharmacyDao().updateUserFlagged(
                        pharmacyId = pharmacyId,
                        isFlagged = pharmacyUserFlaggedData.flagged
                    )
                    pharmacy = pharmacistDb.pharmacyDao().getPharmacyById(pharmacyId).toPharmacy()
                }
            },
            onPharmacyUserFavorited = { pharmacyUserFavoritedData ->
                viewModelScope.launch {
                    pharmacistDb.pharmacyDao().updateUserMarkedAsFavorite(
                        pharmacyId = pharmacyId,
                        isFavorite = pharmacyUserFavoritedData.favorited
                    )
                    pharmacy = pharmacistDb.pharmacyDao().getPharmacyById(pharmacyId).toPharmacy()
                }
            },
            onMedicineStock = { medicineStockData ->
                Log.d(
                    "RealTimeUpdatesService",
                    "Received medicine stock update: $medicineStockData"
                )
                viewModelScope.launch {
                    pharmacistDb.pharmacyDao().upsertPharmacyMedicine(
                        PharmacyMedicineEntity(
                            pharmacyId = pharmacyId,
                            medicineId = medicineStockData.medicineId,
                            stock = medicineStockData.stock
                        )
                    )
                    invalidatingPagingSourceFactory.invalidate()
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
                RealTimeUpdateSubscription.pharmacyUserFavorited(pharmacyId),
            )
        )
        loadingState = LOADED
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
                pharmacistDb.medicineDao().upsertMedicine(result.data.toMedicineEntity())
                invalidatingPagingSourceFactory.invalidate()

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

    fun invalidate() {
        invalidatingPagingSourceFactory.invalidate()
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

    companion object {
        private const val PAGE_SIZE = 10
        private const val PREFETCH_DISTANCE = 1
    }
}

