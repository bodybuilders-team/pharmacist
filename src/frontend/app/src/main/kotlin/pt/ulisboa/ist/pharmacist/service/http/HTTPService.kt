package pt.ulisboa.ist.pharmacist.service.http

import android.content.Context
import android.content.Intent
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.net.HttpURLConnection
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import pt.ulisboa.ist.pharmacist.PharmacistApplication.Companion.API_ENDPOINT
import pt.ulisboa.ist.pharmacist.service.http.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.http.connection.UnexpectedResponseException
import pt.ulisboa.ist.pharmacist.service.http.connection.getBodyOrThrow
import pt.ulisboa.ist.pharmacist.service.http.connection.send
import pt.ulisboa.ist.pharmacist.service.http.media.Problem.Companion.problemMediaType
import pt.ulisboa.ist.pharmacist.service.http.utils.SerializationUtils
import pt.ulisboa.ist.pharmacist.service.http.utils.fromJson
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateTo

/**
 * A service that communicates with a HTTP server.
 *
 * @property API_ENDPOINT the base URL of the API
 * @property httpClient the HTTP client used to communicate with the server
 */
abstract class HTTPService(
    val context: Context,
    val sessionManager: SessionManager,
    val httpClient: OkHttpClient
) {

    /**
     * Sends a HTTP request to the server and parses the response into a [APIResult] of the specified type.
     *
     * @receiver the HTTP request to send
     *
     * @return the result of the request
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    protected suspend inline fun <reified T> Request.getResponseResult(): APIResult<T> =
        this.send(httpClient) { response ->
            if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                sessionManager.clearSession()

                context.navigateTo<HomeActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }

            response.getBodyOrThrow().use { body ->
                val contentType = body.contentType()

                try {
                    when {
                        response.isSuccessful && contentType == applicationJsonMediaType ->
                            APIResult.Success(
                                data = SerializationUtils.gson.fromJson<T>(
                                    JsonReader(
                                        body.charStream()
                                    )
                                )
                            )

                        !response.isSuccessful && contentType == problemMediaType ->
                            APIResult.Failure(
                                error = SerializationUtils.gson.fromJson(
                                    JsonReader(
                                        body.charStream()
                                    )
                                )
                            )

                        response.isSuccessful &&
                                T::class.java.isAssignableFrom(Unit::class.java) ->
                            APIResult.Success(data = Unit as T)

                        else -> throw UnexpectedResponseException(response)
                    }
                } catch (e: JsonSyntaxException) {
                    throw UnexpectedResponseException(response)
                }
            }
        }

    /**
     * Sends a GET request to the specified link.
     *
     * @param link the link to send the request to
     *
     * @return the result of the request
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    protected suspend inline fun <reified T> get(link: String): APIResult<T> =
        Request.Builder()
            .url(API_ENDPOINT + link)
            .build().getResponseResult()

    /**
     * Sends a GET request to the specified link with a token in the header.
     *
     * @param link the link to send the request to
     * @param token the token to send in the header
     *
     * @return the result of the request
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    protected suspend inline fun <reified T> get(
        link: String,
        token: String
    ): APIResult<T> =
        Request.Builder()
            .url(url = API_ENDPOINT + link)
            .header(name = AUTHORIZATION_HEADER, value = "$TOKEN_TYPE $token")
            .build()
            .getResponseResult()

    /**
     * Sends a POST request to the specified link with the specified body.
     *
     * @param link the link to send the request to
     * @param body the body to send in the request
     *
     * @return the result of the request
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    protected suspend inline fun <reified T> post(
        link: String,
        body: Any
    ): APIResult<T> =
        Request.Builder()
            .url(url = API_ENDPOINT + link)
            .post(
                body = SerializationUtils.gson
                    .toJson(body)
                    .toRequestBody(contentType = applicationJsonMediaType)
            )
            .build()
            .getResponseResult()


    /**
     * Sends a POST request to the specified link with the specified body and a token in the header.
     *
     * @param link the link to send the request to
     * @param token the token to send in the header
     * @param body the body to send in the request, if null, an empty request is sent
     *
     * @return the result of the request
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    protected suspend inline fun <reified T> post(
        link: String,
        token: String,
        body: Any? = null
    ): APIResult<T> =
        Request.Builder()
            .url(url = API_ENDPOINT + link)
            .header(name = AUTHORIZATION_HEADER, value = "$TOKEN_TYPE $token")
            .post(
                body = body?.let {
                    SerializationUtils.gson
                        .toJson(body)
                        .toRequestBody(contentType = applicationJsonMediaType)
                } ?: EMPTY_REQUEST
            )
            .build()
            .getResponseResult()

    protected suspend inline fun <reified T> patch(
        link: String,
        token: String,
        body: Any? = null
    ): APIResult<T> =
        Request.Builder()
            .url(url = API_ENDPOINT + link)
            .header(name = AUTHORIZATION_HEADER, value = "$TOKEN_TYPE $token")
            .patch(
                body = body?.let {
                    SerializationUtils.gson
                        .toJson(body)
                        .toRequestBody(contentType = applicationJsonMediaType)
                } ?: EMPTY_REQUEST
            )
            .build()
            .getResponseResult()

    protected suspend inline fun <reified T> put(
        link: String,
        token: String,
        body: Any? = null
    ): APIResult<T> =
        Request.Builder()
            .url(url = API_ENDPOINT + link)
            .header(name = AUTHORIZATION_HEADER, value = "$TOKEN_TYPE $token")
            .put(
                body = body?.let {
                    SerializationUtils.gson
                        .toJson(body)
                        .toRequestBody(contentType = applicationJsonMediaType)
                } ?: EMPTY_REQUEST
            )
            .build()
            .getResponseResult()

    protected suspend inline fun <reified T> delete(
        link: String,
        token: String
    ): APIResult<T> =
        Request.Builder()
            .url(url = API_ENDPOINT + link)
            .header(name = AUTHORIZATION_HEADER, value = "$TOKEN_TYPE $token")
            .delete()
            .build()
            .getResponseResult()

    companion object {
        private const val APPLICATION_JSON = "application/json"
        val applicationJsonMediaType = APPLICATION_JSON.toMediaType()

        const val AUTHORIZATION_HEADER = "Authorization"
        const val TOKEN_TYPE = "Bearer"
    }
}
