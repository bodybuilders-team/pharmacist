package pt.ulisboa.ist.pharmacist.service

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pt.ulisboa.ist.pharmacist.PharmacistApplication
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.repository.local.PharmacistDatabase
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyEntity
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyActivity


class PharmacyNotificationService(
    private val applicationContext: Context,
    private val database: PharmacistDatabase
) {
    private val previousPharmacies = mutableSetOf<Long>()
    private val mutex = Mutex()

    suspend fun verifyNotifications(): Boolean = mutex.withLock {
        val newPharmacies = mutableSetOf<Long>()
        val location = getLocation() ?: return false

        database.pharmacyDao().getAllPharmacies()
            .forEach { pharmacy ->
                if (pharmacyNearMe(
                        location,
                        pharmacy
                    ) && pharmacy.pharmacyId !in previousPharmacies
                ) {
                    showPharmacyNotification(pharmacy.pharmacyId, pharmacy.name)
                    newPharmacies.add(pharmacy.pharmacyId)
                }
            }

        previousPharmacies.clear()
        previousPharmacies.addAll(newPharmacies)

        return true
    }

    private suspend fun getLocation(): Location? {
        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { receivedLocation ->
                    receivedLocation?.let {
                        continuation.resume(it)
                    }
                }
        }
    }

    private fun pharmacyNearMe(location: Location, pharmacy: PharmacyEntity): Boolean {
        val pharmacyLocation = Location("pharmacy")
        pharmacyLocation.latitude = pharmacy.latitude
        pharmacyLocation.longitude = pharmacy.longitude

        return location.distanceTo(pharmacyLocation) <= MAX_DISTANCE_METERS
    }

    private fun showPharmacyNotification(pharmacyId: Long, pharmacyName: String) {
        val notificationIntent = PharmacyActivity.getNavigationIntent(
            applicationContext,
            pharmacyId
        )

        //TODO: Check what happens when the user clicks on the back button in the MedicineActivity after clicking on the notification
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingNotiIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val message = applicationContext.getString(
            R.string.pharmacy_notification_message,
            pharmacyName,
        )

        // Show notification to the user
        val notificationCompat = NotificationCompat.Builder(
            applicationContext,
            PharmacistApplication.MEDICINE_NOTIFICATION_CHANNEL
        )
            .setSmallIcon(R.drawable.pharmacy_logo)
            .setContentTitle(
                applicationContext.getString(
                    R.string.pharmacy_notification_message,
                    pharmacyName
                )
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingNotiIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            if (!checkNotificationPermission()) {
                return@with
            }
            notify(PHARMACY_NOTIFICATION_ID, notificationCompat)
        }
    }

    private fun checkNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }


    companion object {
        private const val PHARMACY_NOTIFICATION_ID = 1
        private const val MAX_DISTANCE_METERS = 100
    }
}