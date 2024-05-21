package pt.ulisboa.ist.pharmacist.repository.remote.pharmacies

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import okio.IOException
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.mappers.toPharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess

@OptIn(ExperimentalPagingApi::class)
class PharmacyRemoteMediator(
    private val pharmacistDb: PharmacistDatabase,
    private val pharmacyApi: PharmacyApi,
    private val medicineId: Long,
    private val location: Location?
) : RemoteMediator<Int, PharmacyEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PharmacyEntity>
    ): MediatorResult {
        val offset = state.pages.flatten().size
        val limit = state.config.pageSize

        return try {
            val result = pharmacyApi.getPharmacies(
                medicineId = medicineId,
                location = location,
                orderBy = "distance",
                limit = limit.toLong(),
                offset = offset.toLong()
            )

            if (!result.isSuccess()) {
                return MediatorResult.Error(Exception("Error loading data"))
            }

            pharmacistDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pharmacistDb.pharmacyDao().clearAllPharmacies()
                }
                pharmacistDb.pharmacyDao().upsertPharmacies(result.data.pharmacies.map {
                    it.toPharmacyEntity()
                })
                pharmacistDb.medicineDao().upsertPharmacyMedicineList(
                    result.data.pharmacies.map {
                        PharmacyMedicineEntity(
                            medicineId = medicineId,
                            pharmacyId = it.pharmacy.pharmacyId,
                            stock = null
                        )
                    }
                )
            }

            MediatorResult.Success(
                endOfPaginationReached =
                result.data.pharmacies.isEmpty() || result.data.pharmacies.size < limit
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