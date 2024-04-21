package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.listAvailableMedicines.MedicineStockModel

class PharmacyMedicinesPagingSource(
    private val pharmaciesService: PharmaciesService,
    private val pageSize: Int,
    private val pid: Long
) : PagingSource<Int, MedicineStockModel>() {
    override fun getRefreshKey(state: PagingState<Int, MedicineStockModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MedicineStockModel> {
        val currentPage = params.key ?: 1
        val offset =
            if (params.key != null) ((currentPage - 1) * pageSize) + 1 else INITIAL_LOAD_SIZE

        val result = pharmaciesService.listAvailableMedicines(
            pharmacyId = pid,
            limit = params.loadSize.toLong(),
            offset = offset.toLong() - 1
        )

        return if (result.isSuccess()) {
            LoadResult.Page(
                data = result.data.medicines,
                prevKey = null,
                nextKey = if (result.data.medicines.isEmpty()) null else currentPage + (params.loadSize / pageSize)
            )
        } else {
            LoadResult.Error(Exception("Error loading data"))
        }
    }

    companion object {
        const val INITIAL_LOAD_SIZE = 1
    }
}