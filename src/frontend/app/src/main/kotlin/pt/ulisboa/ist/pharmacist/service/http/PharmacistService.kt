package pt.ulisboa.ist.pharmacist.service.http

import android.content.Context
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.MedicinesService
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.service.http.services.upload.UploaderService
import pt.ulisboa.ist.pharmacist.service.http.services.users.UsersService
import pt.ulisboa.ist.pharmacist.session.SessionManager

/**
 * The service that handles the pharmacist requests.
 *
 * @param context the context
 * @param httpClient the HTTP client
 * @param sessionManager the session manager
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
