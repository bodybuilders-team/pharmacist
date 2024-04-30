package pt.ulisboa.ist.pharmacist.service.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RealTimeUpdatesBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // No need to specify type of intent since the service is only started once
        val serviceIntent = Intent(context, RealTimeUpdatesBackgroundService::class.java)
        context?.startService(serviceIntent)
    }
}