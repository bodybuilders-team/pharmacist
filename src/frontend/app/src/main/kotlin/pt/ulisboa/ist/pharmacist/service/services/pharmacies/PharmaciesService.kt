package pt.ulisboa.ist.pharmacist.service.services.pharmacies

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.getPharmacies.GetPharmaciesOutputModel
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.listAvailableMedicines.ListAvailableMedicinesOutputModel
import pt.ulisboa.ist.pharmacist.service.utils.Uris

class PharmaciesService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    suspend fun getPharmacies(
        mid: Long? = null,
        limit: Long? = null,
        offset: Long? = null
    ): APIResult<GetPharmaciesOutputModel> {
        return get<GetPharmaciesOutputModel>(link = Uris.getPharmacies(mid, limit, offset))
    }

    suspend fun getPharmacyById(id: Long): APIResult<Pharmacy> {
        return get<Pharmacy>(link = Uris.getPharmacyById(id))
    }

    suspend fun listAvailableMedicines(
        pid: Long,
        limit: Long? = null,
        offset: Long? = null
    ): APIResult<ListAvailableMedicinesOutputModel> {
        return get<ListAvailableMedicinesOutputModel>(
            link = Uris.listAvailableMedicines(
                pid,
                limit,
                offset
            )
        )
    }
}
