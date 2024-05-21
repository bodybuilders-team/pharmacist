package pt.ulisboa.ist.pharmacist.repository.remote.medicines

import android.content.Context
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.repository.network.HTTPService
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager
import javax.inject.Inject

class MedicineApi @Inject constructor(
    context: Context,
    httpClient: OkHttpClient,
    sessionManager: SessionManager
) : HTTPService(context, sessionManager, httpClient) {

    suspend fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: Location?,
        limit: Long,
        offset: Long
    ): APIResult<GetMedicinesWithClosestPharmacyOutputDto> {
        return get<GetMedicinesWithClosestPharmacyOutputDto>(
            link = Uris.medicines(
                substring,
                location,
                limit,
                offset
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    suspend fun getMedicineById(medicineId: Long): APIResult<GetMedicineOutputDto> {
        return get<GetMedicineOutputDto>(
            link = Uris.medicineById(medicineId),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    suspend fun createMedicine(
        name: String,
        description: String,
        boxPhotoUrl: String
    ): APIResult<CreateMedicineOutputDto> {
        return post(
            link = Uris.MEDICINES,
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = CreateMedicineInputDto(
                name = name,
                description = description,
                boxPhotoUrl = boxPhotoUrl
            )
        )

    }

    suspend fun addMedicineNotification(medicineId: Long) =
        put<Unit>(
            link = Uris.medicineNotification(
                sessionManager.userId ?: throw IllegalStateException("No user id"), medicineId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )

    suspend fun removeMedicineNotification(medicineId: Long) =
        delete<Unit>(
            link = Uris.medicineNotification(
                sessionManager.userId ?: throw IllegalStateException("No user id"), medicineId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
}
