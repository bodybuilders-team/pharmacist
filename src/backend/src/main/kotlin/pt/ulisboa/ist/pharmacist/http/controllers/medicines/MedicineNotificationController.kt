package pt.ulisboa.ist.pharmacist.http.controllers.medicines

import java.util.concurrent.Executors
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.medicines.MedicineNotificationService
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest

/**
 * A Medicine Notification Controller.
 *
 * @property medicineNotificationService the medicine notification service
 * @property usersRepository the users repository
 */
@RestController
@RequestMapping(produces = ["application/json"])
class MedicineNotificationController(
    val medicineNotificationService: MedicineNotificationService,
    val usersRepository: UsersRepository
) {

    /**
     * Stream medicine notifications to the user.
     */
    @GetMapping(Uris.MEDICINE_NOTIFICATIONS)
    fun streamSseMvc(): SseEmitter {
        val emitter = SseEmitter()
        val sseMvcExecutor = Executors.newSingleThreadExecutor()

        // TODO obtain user from the request
        val user = usersRepository.findAll(OffsetPageRequest(0, 1))
            .first() ?: throw IllegalStateException("No users found")

        sseMvcExecutor.execute {
            try {
                medicineNotificationService.tryToNotifyUser(user) { notification ->
                    val event = SseEmitter.event()
                        .data(notification).build()

                    emitter.send(event)
                }
            } catch (ex: Exception) {
                emitter.completeWithError(ex)
            }
        }

        return emitter
    }
}