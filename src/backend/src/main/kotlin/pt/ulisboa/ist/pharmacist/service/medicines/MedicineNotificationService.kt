package pt.ulisboa.ist.pharmacist.service.medicines

import kotlinx.coroutines.flow.MutableSharedFlow
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineNotification
import pt.ulisboa.ist.pharmacist.domain.users.User

/**
 * Service that handles the business logic of medicine notifications.
 */
interface MedicineNotificationService {

    fun getFlow(userId: Long): MutableSharedFlow<MedicineNotification>

    /**
     * Tries to notify the user.
     *
     * @param user the user
     * @param notifyAction the action to notify the user
     */
    suspend fun notifyUser(user: User, notifyAction: (MedicineNotification) -> Unit)
}