package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import kotlin.math.max

class PharmaciesPagingSource(
    private val pharmaciesService: PharmaciesService,
    private val mid: Long? = null,
    private val location: Location? = null
) : PagingSource<Int, PharmacyWithUserDataModel>() {

    override fun getRefreshKey(state: PagingState<Int, PharmacyWithUserDataModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val pharmacy = state.closestItemToPosition(anchorPosition) ?: return null
        return max(
            STARTING_KEY,
            (pharmacy.pharmacy.pharmacyId - (state.config.pageSize / 2)).toInt()
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PharmacyWithUserDataModel> {
        val offset = params.key ?: STARTING_KEY
        val limit = params.loadSize

        val result = pharmaciesService.getPharmacies(
            medicineId = mid,
            location = location,
            orderBy = "distance", // TODO: Check if the results are sorted by distance
            limit = limit.toLong(),
            offset = offset.toLong()
        )

        return if (result.isSuccess()) {
            LoadResult.Page(
                data = result.data.pharmacies,
                prevKey = when (offset) {
                    STARTING_KEY -> null
                    else -> max(STARTING_KEY, offset - limit)
                },
                nextKey =
                if (result.data.pharmacies.isEmpty() || result.data.pharmacies.size < limit)
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