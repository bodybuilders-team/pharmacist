package pt.ulisboa.ist.pharmacist.service.real_time_updates

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
import pt.ulisboa.ist.pharmacist.DependenciesContainer
import pt.ulisboa.ist.pharmacist.PharmacistApplication
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.service.utils.runNewBlocking
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity

class MedicineNotificationsBackgroundService : Service() {
    private val dependenciesContainer by lazy {
        (application as DependenciesContainer)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runNewBlocking { // TODO should we make non blocking?
            getUpdates()
        }

        return START_STICKY
    }

    /**
     * Get the real time updates from the server.
     */
    private suspend fun getUpdates() {
        Log.d(TAG, "Started listening for medicine notifications from the server")
        dependenciesContainer.realTimeUpdatesService.listenForRealTimeUpdates(
            onMedicineNotification = { medicineNotificationData ->
                if (checkNotificationPermission()) {
                    showMedicineNotification(medicineNotificationData)
                }
            }
        )
        Log.d(TAG, "Finished listening for medicine notifications from the server")
    }

    private fun showMedicineNotification(notification: MedicineNotificationData) {
        val notificationIntent = MedicineActivity.getNavigationIntent(
            this@MedicineNotificationsBackgroundService,
            notification.medicineStock.medicine.medicineId
        )

        //TODO: Check what happens when the user clicks on the back button in the MedicineActivity after clicking on the notification
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingNotiIntent: PendingIntent = PendingIntent.getActivity(
            this@MedicineNotificationsBackgroundService,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val message = getString(
            R.string.medicine_notification_text,
            notification.medicineStock.medicine.name,
            notification.medicineStock.stock,
            notification.pharmacy.pharmacyName
        )

        // Show notification to the user
        val notificationCompat = NotificationCompat.Builder(
            this@MedicineNotificationsBackgroundService,
            PharmacistApplication.MEDICINE_NOTIFICATION_CHANNEL
        )
            .setSmallIcon(R.drawable.pharmacy_logo)
            .setContentTitle(getString(R.string.medicine_notification_title, notification.medicineStock.medicine.name))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingNotiIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this@MedicineNotificationsBackgroundService)) {
            if (!checkNotificationPermission()) {
                return@with
            }
            notify(NOTIFICATION_ID, notificationCompat)
        }
    }

    private fun checkNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this@MedicineNotificationsBackgroundService,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }


    companion object {
        private const val NOTIFICATION_ID = 1
        const val TAG = "MedicineNotificationsBackgroundService"
    }
}