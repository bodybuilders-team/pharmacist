package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateSubscription
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
import kotlin.math.max

class PharmacyMedicinesPagingSource(
    private val pharmaciesService: PharmaciesService,
    private val realTimeUpdatesService: RealTimeUpdatesService,
    private val pid: Long
) : PagingSource<Int, pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.listAvailableMedicines.MedicineStockModel>() {

    override fun getRefreshKey(state: PagingState<Int, pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.listAvailableMedicines.MedicineStockModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val medicine = state.closestItemToPosition(anchorPosition) ?: return null
        return max(
            STARTING_KEY,
            (medicine.medicine.medicineId - (state.config.pageSize / 2)).toInt()
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.listAvailableMedicines.MedicineStockModel> {
        val offset = params.key ?: STARTING_KEY
        val limit = params.loadSize

        Log.d(
            "RealTimeUpdatesService",
            "Loading medicines for pharmacy $pid with offset $offset and limit $limit"
        )
        val result = pharmaciesService.listAvailableMedicines(
            pharmacyId = pid,
            limit = limit.toLong(),
            offset = offset.toLong()
        )

        return if (result.isSuccess()) {
            realTimeUpdatesService.subscribeToUpdates(
                result.data.medicines.map {
                    RealTimeUpdateSubscription.pharmacyMedicineStock(
                        pharmacyId = pid,
                        medicineId = it.medicine.medicineId
                    )
                }
            )
            LoadResult.Page(
                data = result.data.medicines,
                prevKey = when (offset) {
                    STARTING_KEY -> null
                    else -> max(STARTING_KEY, offset - limit)
                },
                nextKey =
                if (result.data.medicines.isEmpty() || result.data.medicines.size < limit)
                    null
                else
                    offset + limit
            )
        } else {
            LoadResult.Error(Exception("Error loading data"))
        }
    }

    companion object {
        const val STARTING_KEY = 0

    }
}