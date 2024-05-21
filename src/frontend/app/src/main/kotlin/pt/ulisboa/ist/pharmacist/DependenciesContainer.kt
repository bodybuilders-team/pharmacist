package pt.ulisboa.ist.pharmacist

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.session.SessionManager

/**
 * Provides the dependencies of the application.
 *
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 * @property sessionManager the manager used to handle the user session
 * @property realTimeUpdatesService the service used to handle the real-time updates
 * @property httpClient the HTTP client used to make requests
 */
interface DependenciesContainer {
    val jsonEncoder: Gson
    val sessionManager: SessionManager
    val realTimeUpdatesService: RealTimeUpdatesService
    val httpClient: OkHttpClient
}
