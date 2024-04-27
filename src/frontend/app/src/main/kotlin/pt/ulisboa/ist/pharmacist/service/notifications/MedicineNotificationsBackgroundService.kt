package pt.ulisboa.ist.pharmacist.service.notifications

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.takeWhile
import pt.ulisboa.ist.pharmacist.DependenciesContainer
import pt.ulisboa.ist.pharmacist.PharmacistApplication
import pt.ulisboa.ist.pharmacist.PharmacistApplication.Companion.API_ENDPOINT
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.MedicineNotification
import pt.ulisboa.ist.pharmacist.service.http.services.medicines.MedicineNotificationService
import pt.ulisboa.ist.pharmacist.service.utils.runNewBlocking
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity

class MedicineNotificationsBackgroundService : Service() {
    private val dependenciesContainer by lazy {
        (application as DependenciesContainer)
    }

    //TODO: Check null pointer exception
    private val medicineNotificationService by lazy {
        MedicineNotificationService(
            apiEndpoint = API_ENDPOINT,
            sessionManager = dependenciesContainer.sessionManager,
            httpClient = dependenciesContainer.httpClient,
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runNewBlocking {
            while (true) {
                if (dependenciesContainer.sessionManager.isLoggedIn() && checkNotiPermission())
                    getNotifications()

                delay(5000) //TODO: Remove delay
            }
        }

        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotiPermission(): Boolean =
        ActivityCompat.checkSelfPermission(
            this@MedicineNotificationsBackgroundService,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Get the notifications from the server and show them to the user.
     */
    private suspend fun getNotifications() {
        val flow = medicineNotificationService
            .getUpdateFlow<MedicineNotification>()

        flow.takeWhile { dependenciesContainer.sessionManager.isLoggedIn() }
            .collect { notification ->
                val notiIntent = MedicineActivity.getNavigationIntent(
                    this@MedicineNotificationsBackgroundService,
                    notification.medicineStock.medicine.medicineId
                )

                //TODO: Check what happens when the user clicks on the back button in the MedicineActivity after clicking on the notification
                notiIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                val pendingNotiIntent: PendingIntent = PendingIntent.getActivity(
                    this@MedicineNotificationsBackgroundService,
                    0,
                    notiIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )


                // Show notification to the user
                val notificationCompat = NotificationCompat.Builder(
                    this@MedicineNotificationsBackgroundService,
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

                with(NotificationManagerCompat.from(this@MedicineNotificationsBackgroundService)) {
                    if (ActivityCompat.checkSelfPermission(
                            this@MedicineNotificationsBackgroundService,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@with
                    }
                    notify(NOTIFICATION_ID, notificationCompat)
                }
            }
    }


    companion object {
        private const val NOTIFICATION_ID = 1
    }
}