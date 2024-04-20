package pt.ulisboa.ist.pharmacist.service.services.medicines

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.utils.Uris

class MedicinesService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    suspend fun getMedicines(
        substring: String,
        location: String,
        limit: Long,
        offset: Long
    ): APIResult<GetMedicinesWithClosestPharmacyOutputModel> {
        return get<GetMedicinesWithClosestPharmacyOutputModel>(
            link = Uris.getMedicines(
                substring,
                location,
                limit,
                offset
            )
        )
    }

}

data class GetMedicinesWithClosestPharmacyOutputModel(
    val totalCount: Int,
    val medicines: List<MedicineWithClosestPharmacyOutputModel>
)

data class MedicineWithClosestPharmacyOutputModel(
    val medicine: Medicine,
    val closestPharmacy: Pharmacy?
)
