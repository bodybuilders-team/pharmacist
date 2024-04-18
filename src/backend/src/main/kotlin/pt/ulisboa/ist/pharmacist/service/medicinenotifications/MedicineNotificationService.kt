package pt.ulisboa.ist.pharmacist.service.medicinenotifications

import pt.ulisboa.ist.pharmacist.domain.medicinenotifications.MedicineNotification
import pt.ulisboa.ist.pharmacist.domain.users.User

interface MedicineNotificationService {
    fun findNotifications(user: User): List<MedicineNotification>
}