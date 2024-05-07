package pt.ulisboa.ist.pharmacist.service.real_time_updates

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MedicineNotificationsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // No need to specify type of intent since the service is only started once
        val serviceIntent = Intent(context, MedicineNotificationsBackgroundService::class.java)
        context?.startService(serviceIntent)
    }
}