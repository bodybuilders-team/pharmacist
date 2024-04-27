package pt.ulisboa.ist.pharmacist.service

import android.content.Context
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicinesService
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.service.services.users.UsersService
import pt.ulisboa.ist.pharmacist.service.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager

/**
 * The service that handles the pharmacist requests.
 *
 * @param apiEndpoint the API endpoint
 * @param httpClient the HTTP client
 *
 * @property usersService the service that handles the users
 * @property pharmaciesService the service that handles the pharmacies
 * @property medicinesService the service that handles the medicines
 */
class PharmacistService(
    context: Context,
    httpClient: OkHttpClient,
    sessionManager: SessionManager
) : HTTPService(context, sessionManager, httpClient) {

    val uploaderService = UploaderService(context, httpClient, sessionManager)
    val usersService = UsersService(context, httpClient, sessionManager)
    val pharmaciesService = PharmaciesService(context, httpClient, sessionManager)
    val medicinesService = MedicinesService(context, httpClient, sessionManager)
}

class UploaderService(
    context: Context,
    httpClient: OkHttpClient,
    sessionManager: SessionManager
) : HTTPService(context, sessionManager, httpClient) {

    suspend fun uploadBoxPhoto(
        signedUrl: String,
        boxPhoto: ByteArray,
        mimeType: MediaType
    ): APIResult<Unit> {
        return Request.Builder()
            .url(url = signedUrl)
            .put(
                body = boxPhoto
                    .toRequestBody(contentType = mimeType)
            )
            .build()
            .getResponseResult()
    }

    suspend fun createSignedUrl(
        mimeType: String
    ): APIResult<SignedUrlOutputModel> {
        return post(
            link = Uris.CREATE_SIGNED_URL,
            body = SignedUrlInputModel(mimeType = mimeType),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }
}

data class SignedUrlOutputModel(
    val signedUrl: String,
    val url: String
)

data class SignedUrlInputModel(
    val mimeType: String
)