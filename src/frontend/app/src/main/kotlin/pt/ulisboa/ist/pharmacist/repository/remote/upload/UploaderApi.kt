package pt.ulisboa.ist.pharmacist.repository.remote.upload

import android.content.Context
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pt.ulisboa.ist.pharmacist.repository.network.HTTPService
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager
import javax.inject.Inject

/**
 * The api service that handles the upload requests.
 *
 * @param context the context
 * @param httpClient the HTTP client
 * @param sessionManager the session manager
 */
class UploaderApi @Inject constructor(
    context: Context,
    httpClient: OkHttpClient,
    sessionManager: SessionManager
) : HTTPService(context, sessionManager, httpClient) {

    /**
     * Uploads a box photo.
     *
     * @param signedUrl the signed URL
     * @param boxPhoto the box photo
     * @param mimeType the MIME type
     *
     * @return the result of the request
     */
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

    /**
     * Creates a signed URL.
     *
     * @param mimeType the MIME type
     *
     * @return the result of the request
     */
    suspend fun createSignedUrl(
        mimeType: String
    ): APIResult<SignedUrlOutputDto> {
        return post(
            link = Uris.CREATE_SIGNED_URL,
            body = SignedUrlInputDto(mimeType = mimeType),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
    }
}