package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.network.services.medicines.MedicinesService
import pt.ulisboa.ist.pharmacist.repository.network.services.medicines.models.getMedicinesWithClosestPharmacy.MedicineWithClosestPharmacyOutputModel
import kotlin.math.max

class MedicinePagingSource(
    private val medicinesService: MedicinesService,
    private val query: String,
    private val location: Location?
) : PagingSource<Int, MedicineWithClosestPharmacyOutputModel>() {


    override fun getRefreshKey(state: PagingState<Int, MedicineWithClosestPharmacyOutputModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val medicine = state.closestItemToPosition(anchorPosition) ?: return null
        return max(
            STARTING_KEY,
            (medicine.medicine.medicineId - (state.config.pageSize / 2)).toInt()
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MedicineWithClosestPharmacyOutputModel> {
        val offset = params.key ?: STARTING_KEY
        val limit = params.loadSize

        val result = medicinesService.getMedicinesWithClosestPharmacy(
            substring = query,
            location = location,
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