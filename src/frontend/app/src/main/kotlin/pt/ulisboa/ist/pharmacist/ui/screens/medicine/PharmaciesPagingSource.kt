package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.PharmaciesService

class PharmaciesPagingSource(
    private val pharmacysService: PharmaciesService,
    private val mid: Long? = null
) : PagingSource<Int, Pharmacy>() {
    override fun getRefreshKey(state: PagingState<Int, Pharmacy>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pharmacy> {
        val currentPage = params.key ?: 0
        val result = pharmacysService.getPharmacies(
            mid = mid,
            limit = params.loadSize.toLong(),
            offset = currentPage.toLong(),
        )


        return if (result.isSuccess()) {
            LoadResult.Page(
                data = result.data.pharmacies,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (result.data.pharmacies.isEmpty()) null else currentPage + 1
            )
        } else {
            LoadResult.Error(Exception("Error loading data"))
        }
    }
}