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

    override fun getFlow(userId: Long): MutableSharedFlow<MedicineNotification> {
        if (userFlows[userId] == null)
            userFlows[userId] = MutableSharedFlow()

        return userFlows[userId] ?: throw RuntimeException("User flow not found")
    }

    override suspend fun notifyUser(user: User, notifyAction: (MedicineNotification) -> Unit) {
        getFlow(user.userId).collect { notification ->
            notifyAction(notification)
        }
    }

}