package pt.ulisboa.ist.pharmacist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicineNotification
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicineNotificationService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.session.SessionManagerSharedPrefs
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity

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

    val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    override val pharmacistService = PharmacistService(
        apiEndpoint = API_ENDPOINT,
        httpClient = httpClient,
        jsonEncoder = jsonEncoder,
        sessionManager = sessionManager
    )

    val medicineNotificationService = MedicineNotificationService(
        PharmacistApplication.API_ENDPOINT,
        httpClient
    )

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationsChannel()
        }

//        runBlocking {
//            medicineNotificationService.getUpdateFlow<MedicineNotification>().collect { notification ->
//                // Show notification to the user
//                var builder = NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.notification_icon)
//                    .setContentTitle("My notification")
//                    .setContentText("Much longer text that cannot fit one line...")
//                    .setStyle(NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//                with(NotificationManagerCompat.from(this)) {
//                    // notificationId is a unique int for each notification that you must define
//                    MedicineActivity.navigate(this, notification.medicineStock.medicine.medicineId)
//                }
//            }
//
//        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationsChannel() {
        val name = getString(R.string.pharmacist_channel_name)
        val descriptionText = getString(R.string.pharmacist_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("PharmacistApp", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val API_ENDPOINT = "http://10.0.2.2:8080"
        const val TAG = "PharmacistApp"
    }
}
