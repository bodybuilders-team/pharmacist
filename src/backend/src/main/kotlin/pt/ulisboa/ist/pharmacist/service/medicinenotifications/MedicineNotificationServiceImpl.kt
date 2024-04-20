package pt.ulisboa.ist.pharmacist.service.medicinenotifications

import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.medicinenotifications.MedicineNotification
import pt.ulisboa.ist.pharmacist.domain.users.User
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
class MedicineNotificationServiceImpl : MedicineNotificationService {
    private val lock = ReentrantLock()

    companion object {
        const val NOTIFICATION_INTERVAL = 1000L
        const val STOCK_NOTIFICATION_THRESHOLD = 0L
    }

    data class MedicinePharmacyPair(
        val medicineId: Long,
        val pharmacyId: Long
    )

    override fun findNotifications(user: User): List<MedicineNotification> = lock.withLock {
        val medicineNotifications = mutableListOf<MedicineNotification>()

        user.favoritePharmacies.forEach { pharmacy ->
            medicineNotifications.addAll(pharmacy.medicines
                .filter { it.medicine in user.medicinesToNotify }
                .map { MedicineNotification(it, pharmacy.pharmacyId) })
        }

        return medicineNotifications
    }

    override fun tryToNotifyUser(user: User, notifyAction: (MedicineNotification) -> Unit) {
        val previousStocks = mutableMapOf<MedicinePharmacyPair, Long>()

        // TODO way to stop the loop

        while (true) {
            val notifications = findNotifications(user)

            for (notification in notifications) {
                val previousStock = previousStocks.put(
                    MedicinePharmacyPair(
                        medicineId = notification.medicineStock.medicine.medicineId,
                        pharmacyId = notification.pharmacyId
                    ),
                    notification.medicineStock.stock
                ) ?: continue

                if (previousStock <= STOCK_NOTIFICATION_THRESHOLD && notification.medicineStock.stock > STOCK_NOTIFICATION_THRESHOLD) {
                    notifyAction(notification)
                }
            }

            Thread.sleep(NOTIFICATION_INTERVAL)
        }
    }
}