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
    private val location: Location?
) : PagingSource<Int, MedicineWithClosestPharmacyOutputModel>() {
    override fun getRefreshKey(state: PagingState<Int, MedicineWithClosestPharmacyOutputModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MedicineWithClosestPharmacyOutputModel> {
        val currentPage = params.key ?: 0
        val result = medicinesService.getMedicinesWithClosestPharmacy(
            query, location,
            params.loadSize.toLong(), currentPage.toLong()
        )

        return if (result.isSuccess())
            LoadResult.Page(
                data = result.data.medicines,
                prevKey = if (currentPage == 0) null else currentPage - 1,  //TODO: Check if there is no bug with the indices and pages
                nextKey = if (result.data.medicines.isEmpty()) null else currentPage + 1
            )
        else
            LoadResult.Error(Exception("Error loading data"))
    }
}