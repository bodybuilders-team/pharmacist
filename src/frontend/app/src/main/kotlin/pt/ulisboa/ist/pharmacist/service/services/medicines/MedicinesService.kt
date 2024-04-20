package pt.ulisboa.ist.pharmacist.service.services.medicines

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.PharmacistApplication.Companion.API_ENDPOINT
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.utils.Uris

class MedicinesService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    suspend fun getMedicines(limit: Long, offset: Long): APIResult<List<Medicine>> {
        return get<List<Medicine>>(link = API_ENDPOINT + Uris.getMedicines(limit, offset))
    }

}
