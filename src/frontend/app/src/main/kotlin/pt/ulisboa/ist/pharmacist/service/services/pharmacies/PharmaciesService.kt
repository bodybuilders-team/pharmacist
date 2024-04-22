package pt.ulisboa.ist.pharmacist.service.services.pharmacies

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.getPharmacies.GetPharmaciesOutputModel
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.listAvailableMedicines.ListAvailableMedicinesOutputModel
import pt.ulisboa.ist.pharmacist.service.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager

/**
 * The service that handles the pharmacies requests.
 *
 * @property apiEndpoint the API endpoint
 * @property httpClient the HTTP client
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 * @property sessionManager the session manager
 */
class PharmaciesService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson,
    val sessionManager: SessionManager
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    /**
     * Gets the pharmacies.
     *
     * @param medicineId the medicine id
     * @param limit the maximum number of pharmacies to return
     * @param offset the number of pharmacies to skip
     *
     * @return the API result of the get pharmacies request
     *
     * @throws IllegalStateException if there is no access token
     */
    suspend fun getPharmacies(
        medicineId: Long? = null,
        limit: Long? = null,
        offset: Long? = null
    ): APIResult<GetPharmaciesOutputModel> {
        return get<GetPharmaciesOutputModel>(
            link = Uris.getPharmacies(
                medicineId = medicineId,
                limit = limit,
                offset = offset
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    /**
     * Gets the pharmacy with the given [id].
     *
     * @param id the pharmacy id
     *
     * @return the API result of the get pharmacy by id request
     *
     * @throws IllegalStateException if there is no access token
     */
    suspend fun getPharmacyById(id: Long): APIResult<PharmacyWithUserDataModel> {
        return get<PharmacyWithUserDataModel>(
            link = Uris.getPharmacyById(id),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    /**
     * Lists the available medicines in the pharmacy with the given [pharmacyId].
     *
     * @param pharmacyId the pharmacy id
     * @param limit the maximum number of medicines to return
     * @param offset the number of medicines to skip
     *
     * @return the API result of the list available medicines request
     *
     * @throws IllegalStateException if there is no access token
     */
    suspend fun listAvailableMedicines(
        pharmacyId: Long,
        limit: Long? = null,
        offset: Long? = null
    ): APIResult<ListAvailableMedicinesOutputModel> {
        return get<ListAvailableMedicinesOutputModel>(
            link = Uris.listAvailableMedicines(
                pharmacyId = pharmacyId,
                limit = limit,
                offset = offset
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    suspend fun ratePharmacy(pharmacyId: Long, rating: Int): APIResult<Unit> {
        return post<Unit>(
            link = Uris.ratePharmacy(pharmacyId),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = rating
        )
    }

    suspend fun changeMedicineStock(
        pharmacyId: Long,
        medicineId: Long,
        operation: MedicineStockOperation,
        stock: Long
    ): APIResult<Unit> {
        return patch<Unit>(
            link = Uris.changeMedicineStock(pharmacyId, medicineId),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = ChangeMedicineStockModel(operation, stock)
        )
    }

    data class ChangeMedicineStockModel(
        val operation: MedicineStockOperation,
        val quantity: Long
    )

    enum class MedicineStockOperation {
        ADD,
        REMOVE
    }
}
