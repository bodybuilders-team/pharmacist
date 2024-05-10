package pt.ulisboa.ist.pharmacist.service.http.services.pharmacies

import android.content.Context
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.http.HTTPService
import pt.ulisboa.ist.pharmacist.service.http.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.addPharmacy.AddPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.changeMedicineStock.ChangeMedicineStockModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.changeMedicineStock.MedicineStockOperation
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacies.GetPharmaciesOutputModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines.ListAvailableMedicinesOutputModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines.MedicineStockModel
import pt.ulisboa.ist.pharmacist.service.http.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager

/**
 * The service that handles the pharmacies requests.
 *
 * @property httpClient the HTTP client
 * @property sessionManager the session manager
 */
class PharmaciesService(
    context: Context,
    httpClient: OkHttpClient,
    sessionManager: SessionManager
) : HTTPService(context, sessionManager, httpClient) {

    /**
     * Gets the pharmacies.
     *
     * @param medicineId the medicine id
     * @param location the location
     * @param limit the maximum number of pharmacies to return
     * @param offset the number of pharmacies to skip
     *
     * @return the API result of the get pharmacies request
     *
     * @throws IllegalStateException if there is no access token
     */
    suspend fun getPharmacies(
        medicineId: Long? = null,
        location: Location? = null,
        orderBy: String? = null,
        limit: Long? = null,
        offset: Long? = null
    ): APIResult<GetPharmaciesOutputModel> {
        return get<GetPharmaciesOutputModel>(
            link = Uris.pharmacies(
                medicineId = medicineId,
                location = location,
                orderBy = orderBy,
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
            link = Uris.pharmacyById(id),
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
            link = Uris.pharmacyMedicines(
                pharmacyId = pharmacyId,
                limit = limit,
                offset = offset
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    suspend fun listAllAvailableMedicines(
        pharmacyId: Long
    ): APIResult<ListAvailableMedicinesOutputModel> {
        var offset = 0L
        val allMedicines = mutableListOf<MedicineStockModel>()

        while (true) {
            val result = listAvailableMedicines(pharmacyId, MAX_LIMIT, offset)
            if (result.isSuccess()) {
                val medicines = result.data.medicines
                if (medicines.isEmpty())
                    break

                allMedicines.addAll(medicines)
                offset += MAX_LIMIT
            } else {
                return APIResult.Failure(result.error)
            }
        }

        return APIResult.Success(ListAvailableMedicinesOutputModel(allMedicines))
    }

    suspend fun ratePharmacy(pharmacyId: Long, rating: Int): APIResult<Unit> {
        return post<Unit>(
            link = Uris.pharmacyRating(pharmacyId),
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
            link = Uris.pharmacyMedicineById(pharmacyId, medicineId),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = ChangeMedicineStockModel(operation, stock)
        )
    }

    suspend fun addNewMedicineToPharmacy(
        pharmacyId: Long,
        medicineId: Long,
        stock: Long
    ): APIResult<Unit> {
        return put<Unit>(
            link = Uris.pharmacyMedicineById(pharmacyId, medicineId),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = AddNewMedicineInputModel(stock)
        )
    }

    /**
     * Adds a new pharmacy.
     *
     * @param name the pharmacy name
     * @param pharmacyPhotoUrl the pharmacy picture's url
     * @param location the pharmacy location
     *
     * @return the API result of the add pharmacy request
     *
     * @throws IllegalStateException if there is no access token
     */
    suspend fun addPharmacy(
        name: String,
        pharmacyPhotoUrl: String,
        location: Location
    ): APIResult<AddPharmacyOutputModel> {
        return post<AddPharmacyOutputModel>(
            link = Uris.PHARMACIES,
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = mapOf(
                "name" to name,
                //"description" to description,
                "pictureUrl" to pharmacyPhotoUrl,
                "location" to location
            )
        )
    }

    companion object {
        private const val MAX_LIMIT = 100L
    }
}

data class AddNewMedicineInputModel(
    val quantity: Long
)
