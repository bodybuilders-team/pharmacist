package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines.MedicineStockModel
import kotlin.math.max

class PharmacyMedicinesPagingSource(
    private val pharmaciesService: PharmaciesService,
    private val pid: Long
) : PagingSource<Int, MedicineStockModel>() {

    override fun getRefreshKey(state: PagingState<Int, MedicineStockModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val medicine = state.closestItemToPosition(anchorPosition) ?: return null
        return max(
            STARTING_KEY,
            (medicine.medicine.medicineId - (state.config.pageSize / 2)).toInt()
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MedicineStockModel> {
        val offset = params.key ?: STARTING_KEY
        val limit = params.loadSize

        val result = pharmaciesService.listAvailableMedicines(
            pharmacyId = pid,
            limit = limit.toLong(),
            offset = offset.toLong()
        )

        return if (result.isSuccess()) {
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