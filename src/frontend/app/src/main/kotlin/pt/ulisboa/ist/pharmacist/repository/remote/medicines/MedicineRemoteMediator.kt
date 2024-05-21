package pt.ulisboa.ist.pharmacist.repository.remote.medicines

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import okio.IOException
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineEntity
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess

@OptIn(ExperimentalPagingApi::class)
class MedicineRemoteMediator(
    private val pharmacistDb: PharmacistDatabase,
    private val medicineApi: MedicineApi,
    private val query: String,
    private val location: Location?
) : RemoteMediator<Int, MedicineEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MedicineEntity>
    ): MediatorResult {
        val offset = state.pages.flatten().size
        val limit = state.config.pageSize

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

            pharmacistDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pharmacistDb.medicineDao().clearAllMedicines()
                }
                pharmacistDb.medicineDao().upsertMedicines(result.data.medicines.map {
                    MedicineEntity(
                        medicineId = it.medicine.medicineId,
                        name = it.medicine.name,
                        description = it.medicine.description,
                        boxPhotoUrl = it.medicine.boxPhotoUrl,
                        closestPharmacy = it.closestPharmacy?.pharmacyId,
                        notificationsActive = false // TODO: find another way instead of overwriting
                    )
                })
            }

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