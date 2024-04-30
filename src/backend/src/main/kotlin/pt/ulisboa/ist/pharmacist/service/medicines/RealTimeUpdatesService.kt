package pt.ulisboa.ist.pharmacist.service.medicines

import kotlinx.coroutines.flow.MutableSharedFlow
import org.springframework.web.socket.WebSocketSession
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePublishing
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePublishingDto
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateSubscriptionDto

/**
 * Service that handles the business logic of medicine notifications.
 */
interface RealTimeUpdatesService {

    fun getUserFlow(userId: Long): MutableSharedFlow<RealTimeUpdatePublishing>

    fun getSessionFlow(session: WebSocketSession): MutableSharedFlow<RealTimeUpdatePublishing>

    fun addSubscription(
        user: User,
        session: WebSocketSession,
        realTimeUpdateSubscriptionDto: RealTimeUpdateSubscriptionDto
    )

    fun publishUpdate(realTimeUpdatePublishing: RealTimeUpdatePublishing)

    /**
     * Sends updates to the session.
     *
     * @param user the user
     * @param session the session to send the updates to
     * @param sendUpdateAction the action to send the update to the user
     */
    suspend fun sendToSession(
        user: User,
        session: WebSocketSession,
        sendUpdateAction: (RealTimeUpdatePublishingDto) -> Unit
    )
}

