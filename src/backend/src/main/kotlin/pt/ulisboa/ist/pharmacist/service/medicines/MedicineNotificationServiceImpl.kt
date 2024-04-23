package pt.ulisboa.ist.pharmacist.service.medicines

import kotlinx.coroutines.flow.MutableSharedFlow
import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineNotification
import pt.ulisboa.ist.pharmacist.domain.users.User

/**
 * Service that handles the business logic of medicine notifications.
 */
@Service
class MedicineNotificationServiceImpl : MedicineNotificationService {
    val userFlows = mutableMapOf<Long, MutableSharedFlow<MedicineNotification>>()

    data class MedicinePharmacyPair(
        val medicineId: Long,
        val pharmacyId: Long
    )

    override fun getFlows(): MutableMap<Long, MutableSharedFlow<MedicineNotification>> {
        return userFlows
    }

    override suspend fun notifyUser(user: User, notifyAction: (MedicineNotification) -> Unit) {
        val previousStocks = mutableMapOf<MedicinePharmacyPair, Long>()

        if (userFlows[user.userId] == null)
            userFlows[user.userId] = MutableSharedFlow()

        userFlows[user.userId]!!.collect { notification ->
            val previousStock = previousStocks.put(
                MedicinePharmacyPair(
                    medicineId = notification.medicineStock.medicine.medicineId,
                    pharmacyId = notification.pharmacyId
                ),
                notification.medicineStock.stock
            ) ?: return@collect

            if (previousStock <= STOCK_NOTIFICATION_THRESHOLD && notification.medicineStock.stock > STOCK_NOTIFICATION_THRESHOLD)
                notifyAction(notification)
        }
    }

    companion object {
        const val STOCK_NOTIFICATION_THRESHOLD = 0L
    }
}