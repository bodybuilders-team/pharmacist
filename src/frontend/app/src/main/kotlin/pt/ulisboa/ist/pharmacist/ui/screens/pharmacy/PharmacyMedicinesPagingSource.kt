package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlin.math.max
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.listAvailableMedicines.MedicineStockModel

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
        val currentOffset = params.key ?: 0

        val result = pharmaciesService.listAvailableMedicines(
            pharmacyId = pid,
            limit = params.loadSize.toLong(),
            offset = currentOffset.toLong()
        )

        return if (result.isSuccess()) {
            LoadResult.Page(
                data = result.data.medicines,
                prevKey = when (currentOffset) {
                    STARTING_KEY -> null
                    else -> max(STARTING_KEY, currentOffset - params.loadSize)
                },
                nextKey = if (result.data.medicines.isEmpty()) null else currentOffset + params.loadSize
            )
        } else {
            LoadResult.Error(Exception("Error loading data"))
        }
    }

    companion object {
        const val STARTING_KEY = 0

    }
}