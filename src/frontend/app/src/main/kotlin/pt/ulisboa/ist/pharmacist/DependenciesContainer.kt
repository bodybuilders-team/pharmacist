package pt.ulisboa.ist.pharmacist

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager

/**
 * Provides the dependencies of the application.
 *
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 * @property sessionManager the manager used to handle the user session
 * @property pharmacistService the service used to handle the pharmacist requests
 */
interface DependenciesContainer {
    val jsonEncoder: Gson
    val sessionManager: SessionManager
    val pharmacistService: PharmacistService
    val httpClient: OkHttpClient
}
