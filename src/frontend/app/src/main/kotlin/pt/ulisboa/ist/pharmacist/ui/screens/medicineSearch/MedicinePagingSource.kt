package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicinesService
import pt.ulisboa.ist.pharmacist.service.services.medicines.models.getMedicinesWithClosestPharmacy.MedicineWithClosestPharmacyOutputModel

class MedicinePagingSource(
    private val medicinesService: MedicinesService,
    private val query: String,
    private val pageSize: Int,
    private val location: Location?
) : PagingSource<Int, MedicineWithClosestPharmacyOutputModel>() {
    override fun getRefreshKey(state: PagingState<Int, MedicineWithClosestPharmacyOutputModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MedicineWithClosestPharmacyOutputModel> {
        val currentPage = params.key ?: 1
        val offset =
            if (params.key != null) ((currentPage - 1) * pageSize) + 1 else INITIAL_LOAD_SIZE

        val result = medicinesService.getMedicinesWithClosestPharmacy(
            substring = query,
            location = location,
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