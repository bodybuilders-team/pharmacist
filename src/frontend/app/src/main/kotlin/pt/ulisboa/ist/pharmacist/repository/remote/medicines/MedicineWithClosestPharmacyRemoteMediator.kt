package pt.ulisboa.ist.pharmacist.repository.remote.medicines

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import okio.IOException
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineDao
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineWithClosestPharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess

@OptIn(ExperimentalPagingApi::class)
class MedicineWithClosestPharmacyRemoteMediator(
    private val pharmacistDb: PharmacistDatabase,
    private val medicineApi: MedicineApi,
    private val query: String,
    private val location: Location?
) : RemoteMediator<Int, MedicineWithClosestPharmacyEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MedicineWithClosestPharmacyEntity>
    ): MediatorResult {
        val offset = when (loadType) {
            LoadType.REFRESH -> STARTING_KEY
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                state.pages.flatten().size
            }
        }

        val limit = state.config.pageSize

        Log.d("MedicineRemoteMediator", "LoadType: $loadType")
        Log.d(
            "MedicineRemoteMediator",
            "Item count: ${state.pages.flatten().map { it.medicineId }}"
        )
        Log.d("MedicineRemoteMediator", "Offset: $offset, Limit: $limit")

        return try {
            val result = medicineApi.getMedicinesWithClosestPharmacy(
                substring = query,
                location = location,
                limit = limit.toLong(),
                offset = offset.toLong()
            )

            if (!result.isSuccess()) {
                return MediatorResult.Error(Exception("Error loading data"))
            }

            Log.d("MedicineRemoteMediator", "Result: ${result.data.medicines.size}")

            pharmacistDb.withTransaction {
                if (loadType == LoadType.REFRESH)
                    pharmacistDb.medicineDao().clearAllMedicines()

                pharmacistDb.medicineDao().upsertBaseMedicines(result.data.medicines.map {
                    MedicineDao.MedicineBaseEntity(
                        medicineId = it.medicine.medicineId,
                        name = it.medicine.name,
                        description = it.medicine.description,
                        boxPhotoUrl = it.medicine.boxPhotoUrl
                    )
                })
            }

            Log.d(
                "MedicineRemoteMediator",
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