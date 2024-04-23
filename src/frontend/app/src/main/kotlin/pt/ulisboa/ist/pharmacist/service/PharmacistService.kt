package pt.ulisboa.ist.pharmacist.service

import com.google.gson.Gson
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
 * @param jsonEncoder the JSON encoder used to serialize/deserialize objects
 *
 * @property usersService the service that handles the users
 * @property pharmaciesService the service that handles the pharmacies
 * @property medicinesService the service that handles the medicines
 */
class PharmacistService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson,
    sessionManager: SessionManager
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    val uploaderService = UploaderService(apiEndpoint, httpClient, jsonEncoder, sessionManager)
    val usersService = UsersService(apiEndpoint, httpClient, jsonEncoder, sessionManager)
    val pharmaciesService = PharmaciesService(apiEndpoint, httpClient, jsonEncoder, sessionManager)
    val medicinesService = MedicinesService(apiEndpoint, httpClient, jsonEncoder, sessionManager)
}

class UploaderService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson,
    val sessionManager: SessionManager
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

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