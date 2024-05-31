package pt.ulisboa.ist.pharmacist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.PharmacyNotificationService
import pt.ulisboa.ist.pharmacist.service.PharmacyNotificationWork
import pt.ulisboa.ist.pharmacist.service.real_time_updates.MedicineNotificationsBackgroundService
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import javax.inject.Inject


/**
 * The Pharmacist application.
 *
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 * @property sessionManager the manager used to handle the user session
 */
@HiltAndroidApp
class PharmacistApplication : DependenciesContainer, Application(), ImageLoaderFactory,
    Configuration.Provider {

    @Inject
    override lateinit var httpClient: OkHttpClient

    @Inject
    override lateinit var jsonEncoder: Gson

    @Inject
    override lateinit var sessionManager: SessionManager

    @Inject
    override lateinit var realTimeUpdatesService: RealTimeUpdatesService

    private val serviceScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationService: PharmacyNotificationService

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationsChannel()

        val serviceIntent = Intent(this, MedicineNotificationsBackgroundService::class.java)
        startService(serviceIntent)

        serviceScope.launch {
            realTimeUpdatesService.startService()
        }

        val workRequest = PeriodicWorkRequestBuilder<PharmacyNotificationWork>(
            PERIODIC_PHARMACY_NOTIFICATION_INTERVAL,
            java.util.concurrent.TimeUnit.MINUTES
        )
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)


        serviceScope.launch {
            while (true) {
                notificationService.verifyNotifications()
                delay(PHARMACY_NOTIFICATIONS_DELAY)
            }
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

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.1)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.03)
                    .directory(cacheDir)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    companion object {
        const val MEDICINE_NOTIFICATION_CHANNEL = "MedicineNotifications"

        private const val API_ENDPOINT_TYPE = "render"
        val API_ENDPOINT = when (API_ENDPOINT_TYPE) {
            "localhost" -> "http://10.0.2.2:8080"
            "ngrok" -> "https://2b02-2001-818-e871-b700-c937-8172-33bf-a88.ngrok-free.app"
            "render" -> "https://pharmacist-e9t4.onrender.com"
            "domain" -> "https://thepharmacist.online"
            else -> {
                throw IllegalStateException("Invalid API_ENDPOINT_TYPE")
            }
        }
        const val TAG = "PharmacistApp"
        private const val PERIODIC_PHARMACY_NOTIFICATION_INTERVAL = 15L
        private const val PHARMACY_NOTIFICATIONS_DELAY = 30_000L
    }
}
