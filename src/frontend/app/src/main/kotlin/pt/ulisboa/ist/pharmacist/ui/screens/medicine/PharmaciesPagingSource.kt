package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.PharmaciesService
import kotlin.math.max

class PharmaciesPagingSource(
    private val pharmaciesService: PharmaciesService,
    private val mid: Long? = null
) : PagingSource<Int, Pharmacy>() {

    override fun getRefreshKey(state: PagingState<Int, Pharmacy>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val pharmacy = state.closestItemToPosition(anchorPosition) ?: return null
        return max(
            STARTING_KEY,
            (pharmacy.pharmacyId - (state.config.pageSize / 2)).toInt()
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pharmacy> {
        val currentOffset = params.key ?: 0

        val result = pharmaciesService.getPharmacies(
            medicineId = mid,
            limit = params.loadSize.toLong(),
            offset = currentOffset.toLong()
        )

        return if (result.isSuccess()) {
            LoadResult.Page(
                data = result.data.pharmacies,
                prevKey = when (currentOffset) {
                    STARTING_KEY -> null
                    else -> max(STARTING_KEY, currentOffset - params.loadSize)
                },
                nextKey = if (result.data.pharmacies.isEmpty()) null else currentOffset + params.loadSize
            )
        } else {
            LoadResult.Error(Exception("Error loading data"))
        }
    }

    companion object {
        const val STARTING_KEY = 0
    }
}