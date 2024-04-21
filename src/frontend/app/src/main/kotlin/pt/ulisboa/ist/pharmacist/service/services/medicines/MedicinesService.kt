package pt.ulisboa.ist.pharmacist.service.services.medicines

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.medicines.models.getMedicinesWithClosestPharmacy.GetMedicinesWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.service.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager

class MedicinesService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson,
    val sessionManager: SessionManager
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    suspend fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: Location?,
        limit: Long,
        offset: Long
    ): APIResult<GetMedicinesWithClosestPharmacyOutputModel> {
        return get<GetMedicinesWithClosestPharmacyOutputModel>(
            link = Uris.getMedicines(
                substring,
                location,
                limit,
                offset
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    suspend fun getMedicineById(pid: Long): APIResult<Medicine> {
        return get<Medicine>(
            link = Uris.getMedicineById(pid),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

}
