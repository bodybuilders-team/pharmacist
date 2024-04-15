package pt.ulisboa.ist.pharmacist

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.session.SessionManagerSharedPrefs

/**
 * The Pharmacist application.
 *
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 * @property sessionManager the manager used to handle the user session
 * @property pharmacistService the service used to handle the pharmacist requests
 */
class PharmacistApplication : DependenciesContainer, Application() {

    override val jsonEncoder: Gson = GsonBuilder().create()

    override val sessionManager: SessionManager = SessionManagerSharedPrefs(context = this)

    override val pharmacistService = PharmacistService(
        apiEndpoint = API_ENDPOINT,
        httpClient = OkHttpClient(),
        jsonEncoder = jsonEncoder
    )

    companion object {
        private const val API_ENDPOINT =
            " https://a409-2001-8a0-6370-f300-fcd0-2e04-8396-8ee5.eu.ngrok.io"
        const val TAG = "PharmacistApp"
    }
}
