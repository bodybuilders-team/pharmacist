package pt.ulisboa.ist.pharmacist.service.http.services.medicines

import android.content.Context
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.http.HTTPService
import pt.ulisboa.ist.pharmacist.service.http.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.models.getMedicinesWithClosestPharmacy.GetMedicinesWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.service.http.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager

class MedicinesService(
    context: Context,
    httpClient: OkHttpClient,
    sessionManager: SessionManager
) : HTTPService(context, sessionManager, httpClient) {

    suspend fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: Location?,
        limit: Long,
        offset: Long
    ): APIResult<GetMedicinesWithClosestPharmacyOutputModel> {
        return get<GetMedicinesWithClosestPharmacyOutputModel>(
            link = Uris.medicines(
                substring,
                location,
                limit,
                offset
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    suspend fun getMedicineById(medicineId: Long): APIResult<Medicine> {
        return get<Medicine>(
            link = Uris.medicineById(medicineId),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }

    suspend fun createMedicine(
        name: String,
        description: String,
        boxPhotoUrl: String
    ): APIResult<CreateMedicineOutputModel> {
        return post(
            link = Uris.MEDICINES,
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = CreateMedicineInputModel(
                name = name,
                description = description,
                boxPhotoUrl = boxPhotoUrl
            )
        )

    }

    data class CreateMedicineInputModel(
        val name: String,
        val description: String,
        val boxPhotoUrl: String
    )

    data class CreateMedicineOutputModel(
        val medicineId: Long
    )
}
