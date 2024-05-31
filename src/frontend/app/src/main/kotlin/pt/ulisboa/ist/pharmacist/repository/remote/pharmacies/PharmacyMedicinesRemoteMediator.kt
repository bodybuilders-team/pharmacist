package pt.ulisboa.ist.pharmacist.repository.remote.pharmacies

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import okio.IOException
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineDao
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineFlatEntity
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateSubscription
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService

@OptIn(ExperimentalPagingApi::class)
class PharmacyMedicinesRemoteMediator(
    private val pharmacistDb: PharmacistDatabase,
    private val pharmacyApi: PharmacyApi,
    private val pharmacyId: Long,
    private val realTimeUpdatesService: RealTimeUpdatesService,
) : RemoteMediator<Int, PharmacyMedicineFlatEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PharmacyMedicineFlatEntity>
    ): MediatorResult {
        val offset = when (loadType) {
            LoadType.REFRESH -> STARTING_KEY
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                state.pages.flatten().size
            }
        }

        val limit = state.config.pageSize

        Log.d("PharmacyMedicinesRemoteMediator", "LoadType: $loadType")
        Log.d(
            "PharmacyMedicinesRemoteMediator",
            "Item count: ${state.pages.flatten().map { it.medicineId }}"
        )
        Log.d("PharmacyMedicinesRemoteMediator", "Offset: $offset, Limit: $limit")

        return try {
            val result = pharmacyApi.listAvailableMedicines(
                pharmacyId = pharmacyId,
                limit = limit.toLong(),
                offset = offset.toLong()
            )

            if (!result.isSuccess()) {
                return MediatorResult.Error(Exception("Error loading data"))
            }

            Log.d("PharmacyMedicinesRemoteMediator", "Result: ${result.data.medicines.size}")

            pharmacistDb.withTransaction {
                if (loadType == LoadType.REFRESH)
                    pharmacistDb.pharmacyDao().clearPharmacyMedicineByPharmacyId(pharmacyId)

                pharmacistDb.medicineDao().upsertBaseMedicines(result.data.medicines.map {
                    MedicineDao.MedicineBaseEntity(
                        medicineId = it.medicine.medicineId,
                        name = it.medicine.name,
                        description = it.medicine.description,
                        boxPhotoUrl = it.medicine.boxPhotoUrl
                    )
                })

                pharmacistDb.pharmacyDao().upsertPharmacyMedicineList(result.data.medicines.map {
                    PharmacyMedicineEntity(
                        pharmacyId = pharmacyId,
                        medicineId = it.medicine.medicineId,
                        stock = it.stock
                    )
                })

                realTimeUpdatesService.subscribeToUpdates(
                    result.data.medicines.map {
                        RealTimeUpdateSubscription.pharmacyMedicineStock(
                            pharmacyId = pharmacyId,
                            medicineId = it.medicine.medicineId
                        )
                    }
                )
            }

            Log.d(
                "PharmacyMedicinesRemoteMediator",
                "Reached end of pagination: ${result.data.medicines.isEmpty() || result.data.medicines.size < limit}"
            )

            MediatorResult.Success(
                endOfPaginationReached =
                result.data.medicines.isEmpty() || result.data.medicines.size < limit
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val STARTING_KEY = 0
    }
}