package pt.ulisboa.ist.pharmacist.service.notifications

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import kotlinx.coroutines.delay
import pt.ulisboa.ist.pharmacist.DependenciesContainer
import pt.ulisboa.ist.pharmacist.PharmacistApplication
import pt.ulisboa.ist.pharmacist.PharmacistApplication.Companion.API_ENDPOINT
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.MedicineNotification
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.RealTimeUpdateTypes.MEDICINE_NOTIFICATION
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.RealTimeUpdateTypes.PHARMACY
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.RealTimeUpdateTypes.PHARMACY_MEDICINE_STOCK
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.service.http.utils.fromJson
import pt.ulisboa.ist.pharmacist.service.utils.runNewBlocking
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity

class RealTimeUpdatesBackgroundService : Service() {
    private val dependenciesContainer by lazy {
        (application as DependenciesContainer)
    }

    //TODO: Check null pointer exception
    private val realTimeUpdatesService by lazy {
        RealTimeUpdatesService(
            apiEndpoint = API_ENDPOINT,
            sessionManager = dependenciesContainer.sessionManager,
            httpClient = dependenciesContainer.httpClient,
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runNewBlocking {
            while (true) {
                if (dependenciesContainer.sessionManager.isLoggedIn()) {
                    getUpdates()
                }

                delay(5000) //TODO: Remove delay
                Log.d(TAG, "Checking if user is logged in after delay")
            }
        }

        return START_STICKY
    }

    /**
     * Get the real time updates from the server.
     */
    private suspend fun getUpdates() {
        Log.d(TAG, "Getting real time updates")
        val flow = realTimeUpdatesService
            .getUpdateFlow()

        Log.d(TAG, "Real time updates flow started")
        flow.collect { realTimeUpdate ->
            when (realTimeUpdate.type) {
                PHARMACY, PHARMACY_MEDICINE_STOCK -> {}
                MEDICINE_NOTIFICATION -> {
                    if (checkNotificationPermission()) {
                        val medicineNotification =
                            Gson().fromJson<MedicineNotification>(realTimeUpdate.data)
                        showMedicineNotification(medicineNotification)
                    }
                }
            }
        }
        Log.d(TAG, "Real time updates flow ended")
    }

    private fun showMedicineNotification(notification: MedicineNotification) {
        val notificationIntent = MedicineActivity.getNavigationIntent(
            this@RealTimeUpdatesBackgroundService,
            notification.medicineStock.medicine.medicineId
        )

        //TODO: Check what happens when the user clicks on the back button in the MedicineActivity after clicking on the notification
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingNotiIntent: PendingIntent = PendingIntent.getActivity(
            this@RealTimeUpdatesBackgroundService,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        // Show notification to the user
        val notificationCompat = NotificationCompat.Builder(
            this@RealTimeUpdatesBackgroundService,
            PharmacistApplication.MEDICINE_NOTIFICATION_CHANNEL
        )
            .setSmallIcon(R.drawable.pharmacy_logo)
            .setContentTitle(getString(R.string.medicine_notification_title))
            .setContentText(
                getString(
                    R.string.medicine_notification_text,
                    notification.medicineStock.medicine.name,
                    notification.medicineStock.stock,
                    notification.pharmacyId
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingNotiIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this@RealTimeUpdatesBackgroundService)) {
            if (!checkNotificationPermission()) {
                return@with
            }
            notify(NOTIFICATION_ID, notificationCompat)
        }
    }

    private fun checkNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this@RealTimeUpdatesBackgroundService,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }


    companion object {
        private const val NOTIFICATION_ID = 1
        const val TAG = "RealTimeUpdatesBackgroundService"
    }
}