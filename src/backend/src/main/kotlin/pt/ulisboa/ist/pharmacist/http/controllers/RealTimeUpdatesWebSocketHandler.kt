package pt.ulisboa.ist.pharmacist.http.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.AuthenticationInterceptor.Companion.AUTHORIZATION_HEADER
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.AuthenticationInterceptor.Companion.parseBearerToken
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.AuthenticationException
import pt.ulisboa.ist.pharmacist.service.medicines.RealTimeUpdatesService
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils
import pt.ulisboa.ist.pharmacist.service.utils.JsonWebSocketHandler

/**
 * A Web Socket Handler for the Real Time Updates.
 *
 * @property realTimeUpdatesService the real time updates service
 * @property usersRepository the users repository
 */
@Component
class RealTimeUpdatesWebSocketHandler(
    private val realTimeUpdatesService: RealTimeUpdatesService,
    private val usersRepository: UsersRepository,
    private val hashingUtils: HashingUtils
) : JsonWebSocketHandler<Array<RealTimeUpdateSubscriptionDto>>(Array<RealTimeUpdateSubscriptionDto>::class.java) {

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    var sendUpdatesJobs: MutableMap<String, Job> = mutableMapOf()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val user = authenticate(session)

        println("Websocket - User ${user.userId} authenticated. Connection established with session ${session.id}")

        sendUpdatesJobs[session.id] = coroutineScope.launch {
            realTimeUpdatesService.sendToSession(user, session) { realTimeUpdate ->
                println("Publishing ${realTimeUpdate.type} update to session ${session.id}")
                if (!session.isOpen) {
                    this.cancel()
                    return@sendToSession
                }

                sendObject(session, realTimeUpdate)
            }
//            while (true) {
//                sendObject(
//                    session, MedicineNotification(
//                        MedicineStock( Medicine(
//                            6,
//                            "Alprazolam",
//                            "Anxiolytic",
//                            "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
//                        ), 10),
//                        1
//                    )
//                )
//                delay(4000)
//            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        println("Websocket - Connection closed with session ${session.id}")
        sendUpdatesJobs[session.id]?.cancel()
        sendUpdatesJobs.remove(session.id)
    }

    override fun handleObject(session: WebSocketSession, obj: Array<RealTimeUpdateSubscriptionDto>) {
        val user = authenticate(session)

        obj.forEach { objElem ->
            realTimeUpdatesService.addSubscription(user, session, objElem)
        }
    }

    private fun authenticate(session: WebSocketSession): User {
        val authHeader = session.handshakeHeaders[AUTHORIZATION_HEADER]?.firstOrNull()
            ?: throw AuthenticationException("Missing authorization token")

        val accessToken = parseBearerToken(authHeader)
            ?: throw AuthenticationException("Token is not a Bearer Token")

        val tokenHash = hashingUtils.hashToken(accessToken)
        val user = usersRepository.findByAccessTokenHash(accessToken = tokenHash)
            ?: throw AuthenticationException("Invalid access token")

        return user
    }
}