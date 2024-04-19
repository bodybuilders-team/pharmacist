package pt.ulisboa.ist.pharmacist.service.services.pharmacies

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.PharmacistApplication.Companion.API_ENDPOINT
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.users.models.getUsers.GetUsersOutput
import pt.ulisboa.ist.pharmacist.service.utils.Uris

class PharmaciesService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    suspend fun getPharmacies(): APIResult<GetUsersOutput> {
        return get<GetUsersOutput>(link = API_ENDPOINT + Uris.PHARMACIES)
    }

    suspend fun getPharmacyById(id: Long): APIResult<Pharmacy> {
        return get<Pharmacy>(link = API_ENDPOINT + Uris.getPharmaciesById(id))
    }

}
