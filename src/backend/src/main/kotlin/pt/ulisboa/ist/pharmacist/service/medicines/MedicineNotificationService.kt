package pt.ulisboa.ist.pharmacist.service.medicines

import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineNotification
import pt.ulisboa.ist.pharmacist.domain.users.User

/**
 * Service that handles the business logic of medicine notifications.
 */
interface MedicineNotificationService {

    /**
     * Finds the notifications for a given user.
     *
     * @param user the user
     * @return the list of notifications
     */
    fun findNotifications(user: User): List<MedicineNotification>

    /**
     * Tries to notify the user.
     *
     * @param user the user
     * @param notifyAction the action to notify the user
     */
    fun tryToNotifyUser(user: User, notifyAction: (MedicineNotification) -> Unit)
}