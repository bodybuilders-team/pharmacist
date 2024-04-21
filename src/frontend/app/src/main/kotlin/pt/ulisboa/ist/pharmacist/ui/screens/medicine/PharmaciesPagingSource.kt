package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.PharmaciesService

class PharmaciesPagingSource(
    private val pharmaciesService: PharmaciesService,
    private val pageSize: Int,
    private val mid: Long? = null
) : PagingSource<Int, Pharmacy>() {
    override fun getRefreshKey(state: PagingState<Int, Pharmacy>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pharmacy> {
        val currentPage = params.key ?: 1
        val offset =
            if (params.key != null) ((currentPage - 1) * pageSize) + 1 else INITIAL_LOAD_SIZE

        val result = pharmaciesService.getPharmacies(
            mid = mid,
            limit = params.loadSize.toLong(),
            offset = offset.toLong() - 1
        )

        return if (result.isSuccess()) {
            LoadResult.Page(
                data = result.data.pharmacies,
                prevKey = null,
                nextKey = if (result.data.pharmacies.isEmpty()) null else currentPage + (params.loadSize / pageSize)
            )
        } else {
            LoadResult.Error(Exception("Error loading data"))
        }
    }

    companion object {
        const val INITIAL_LOAD_SIZE = 1

    }
}