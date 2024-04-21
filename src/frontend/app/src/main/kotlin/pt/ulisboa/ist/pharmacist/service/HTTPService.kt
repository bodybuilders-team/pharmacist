package pt.ulisboa.ist.pharmacist.service

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.connection.UnexpectedResponseException
import pt.ulisboa.ist.pharmacist.service.connection.fromJson
import pt.ulisboa.ist.pharmacist.service.connection.getBodyOrThrow
import pt.ulisboa.ist.pharmacist.service.connection.send
import pt.ulisboa.ist.pharmacist.service.media.Problem.Companion.problemMediaType

/**
 * A service that communicates with a HTTP server.
 *
 * @property apiEndpoint the base URL of the API
 * @property httpClient the HTTP client used to communicate with the server
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 */
abstract class HTTPService(
    protected val apiEndpoint: String,
    protected val httpClient: OkHttpClient,
    val jsonEncoder: Gson
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
            val body = response.getBodyOrThrow()
            val contentType = body.contentType()
            val resJson = JsonReader(body.charStream())

            try {
                when {
                    response.isSuccessful && contentType == applicationJsonMediaType ->
                        APIResult.Success(data = jsonEncoder.fromJson<T>(resJson))

                    !response.isSuccessful && contentType == problemMediaType ->
                        APIResult.Failure(error = jsonEncoder.fromJson(resJson))

                    else -> throw UnexpectedResponseException(response)
                }
            } catch (e: JsonSyntaxException) {
                throw UnexpectedResponseException(response)
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
            .url(apiEndpoint + link)
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
            .url(url = apiEndpoint + link)
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
            .url(url = apiEndpoint + link)
            .post(
                body = jsonEncoder
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
            .url(url = apiEndpoint + link)
            .header(name = AUTHORIZATION_HEADER, value = "$TOKEN_TYPE $token")
            .post(
                body = body?.let {
                    jsonEncoder
                        .toJson(body)
                        .toRequestBody(contentType = applicationJsonMediaType)
                } ?: EMPTY_REQUEST
            )
            .build()
            .getResponseResult()

    companion object {
        private const val APPLICATION_JSON = "application/json"
        val applicationJsonMediaType = APPLICATION_JSON.toMediaType()

        const val AUTHORIZATION_HEADER = "Authorization"
        const val TOKEN_TYPE = "Bearer"
    }
}
