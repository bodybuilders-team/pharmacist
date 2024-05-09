package pt.ulisboa.ist.pharmacist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.real_time_updates.MedicineNotificationsBackgroundService
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
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

    override val httpClient = OkHttpClient.Builder()
        .connectTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
//        .connectionSpecs(listOf(okhttp3.ConnectionSpec.MODERN_TLS))
        .build()

    override val pharmacistService = PharmacistService(
        context = this,
        httpClient = httpClient,
        sessionManager = sessionManager
    )

    override val realTimeUpdatesService = RealTimeUpdatesService(
        apiEndpoint = API_ENDPOINT,
        sessionManager = sessionManager,
        httpClient = httpClient
    )

    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationsChannel()

        val serviceIntent = Intent(this, MedicineNotificationsBackgroundService::class.java)

        startService(serviceIntent)

        serviceScope.launch {
            realTimeUpdatesService.startService()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationsChannel() {
        val name = getString(R.string.medicines_notification_channel_name)
        val descriptionText = getString(R.string.medicines_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(MEDICINE_NOTIFICATION_CHANNEL, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val MEDICINE_NOTIFICATION_CHANNEL = "MedicineNotifications"
        const val API_ENDPOINT = "https://pharmacist-e9t4.onrender.com"
        const val TAG = "PharmacistApp"
    }
}
