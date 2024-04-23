package pt.ulisboa.ist.pharmacist.http.controllers.medicines

import jakarta.validation.Valid
import java.util.concurrent.Executors
import kotlinx.coroutines.runBlocking
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.AuthenticationInterceptor
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.medicines.MedicineNotificationService

/**
 * A Medicine Notification Controller.
 *
 * @property medicineNotificationService the medicine notification service
 * @property usersRepository the users repository
 */
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
@Authenticated
class MedicineNotificationController(
    val medicineNotificationService: MedicineNotificationService,
    val usersRepository: UsersRepository
) {

    /**
     * Stream medicine notifications to the user.
     */
    @GetMapping(Uris.MEDICINE_NOTIFICATIONS)
    fun streamSseMvc(
        @Valid @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User
    ): SseEmitter {
        val emitter = SseEmitter()
        val sseMvcExecutor = Executors.newSingleThreadExecutor()

        sseMvcExecutor.execute {
            runBlocking {
                try {
                    medicineNotificationService.notifyUser(user) { notification ->
                        val event = SseEmitter.event()
                            .data(notification).build()

                        emitter.send(event)
                    }
                } catch (ex: Exception) {
                    emitter.completeWithError(ex)
                }
            }
        }

        return emitter
    }
}