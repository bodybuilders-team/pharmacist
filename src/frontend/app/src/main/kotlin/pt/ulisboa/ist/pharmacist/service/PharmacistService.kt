package pt.ulisboa.ist.pharmacist.service

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.services.users.UsersService

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
    jsonEncoder: Gson
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    val usersService = UsersService(apiEndpoint, httpClient, jsonEncoder)
    val pharmaciesService = PharmaciesService(apiEndpoint, httpClient, jsonEncoder)
    val medicinesService = MedicinesService(apiEndpoint, httpClient, jsonEncoder)
}
