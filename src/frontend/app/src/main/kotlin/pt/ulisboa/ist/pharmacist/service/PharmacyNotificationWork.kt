package pt.ulisboa.ist.pharmacist.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PharmacyNotificationWork @AssistedInject constructor(
    private val service: PharmacyNotificationService,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val success = service.verifyNotifications()

        return if (success) Result.success() else Result.failure()
    }

}