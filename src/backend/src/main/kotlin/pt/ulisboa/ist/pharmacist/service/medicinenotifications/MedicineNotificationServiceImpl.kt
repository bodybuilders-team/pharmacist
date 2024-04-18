package pt.ulisboa.ist.pharmacist.service.medicinenotifications

import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.medicinenotifications.MedicineNotification
import pt.ulisboa.ist.pharmacist.domain.users.User
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
class MedicineNotificationServiceImpl(
) : MedicineNotificationService {
    private val lock = ReentrantLock()


    override fun findNotifications(user: User): List<MedicineNotification> = lock.withLock {
        val medicines = mutableListOf<MedicineNotification>()

        for (pharmacy in user.favoritePharmacies) {
            val medicinesNotis = pharmacy.medicines.filter { it.medicine in user.medicinesToNotify }
                .map { MedicineNotification(it, pharmacy) }
            medicines.addAll(medicinesNotis)
        }

        return medicines
    }
}