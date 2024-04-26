package pt.ulisboa.ist.pharmacist.http.controllers.medicines

import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.AuthenticationInterceptor.Companion.AUTHORIZATION_HEADER
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.AuthenticationInterceptor.Companion.parseBearerToken
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.AuthenticationException
import pt.ulisboa.ist.pharmacist.service.medicines.MedicineNotificationService
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils
import pt.ulisboa.ist.pharmacist.service.utils.JsonWebSocketHandler

/**
 * A Medicine Notification Controller.
 *
 * @property medicineNotificationService the medicine notification service
 * @property usersRepository the users repository
 */
@Component
class MedicineNotificationWebSocketHandler(
    private val medicineNotificationService: MedicineNotificationService,
    private val usersRepository: UsersRepository,
    private val hashingUtils: HashingUtils
) : JsonWebSocketHandler<Unit>(Unit::class.java) {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val authHeader = session.handshakeHeaders[AUTHORIZATION_HEADER]?.firstOrNull()
            ?: throw AuthenticationException("Missing authorization token")

        val accessToken = parseBearerToken(authHeader)
            ?: throw AuthenticationException("Token is not a Bearer Token")

        val tokenHash = hashingUtils.hashToken(accessToken)
        val user = usersRepository.findByAccessTokenHash(accessToken = tokenHash)
            ?: throw AuthenticationException("Invalid access token")

        runBlocking {
            medicineNotificationService.notifyUser(user) { notification ->
                sendObject(session, notification)
            }
        }
    }

    override fun handleObject(session: WebSocketSession?, obj: Unit) {
        return
    }
}