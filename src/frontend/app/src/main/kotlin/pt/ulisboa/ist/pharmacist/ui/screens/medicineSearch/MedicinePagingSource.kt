package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlin.math.max
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicinesService
import pt.ulisboa.ist.pharmacist.service.services.medicines.models.getMedicinesWithClosestPharmacy.MedicineWithClosestPharmacyOutputModel

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
        val currentOffset = params.key ?: 0

        val result = medicinesService.getMedicinesWithClosestPharmacy(
            substring = query,
            location = location,
            limit = params.loadSize.toLong(),
            offset = currentOffset.toLong()
        )

        return if (result.isSuccess()) {
            Log.d(
                "MedicinePagingSource",
                "limit = ${params.loadSize.toLong()}, offset = ${currentOffset.toLong()}, itemCount = ${result.data.medicines.size}"
            )

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