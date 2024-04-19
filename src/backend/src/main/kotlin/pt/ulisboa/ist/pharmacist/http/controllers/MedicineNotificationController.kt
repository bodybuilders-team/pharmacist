package pt.ulisboa.ist.pharmacist.http.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.medicinenotifications.MedicineNotificationService
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest
import java.util.concurrent.Executors

@RestController
@RequestMapping(produces = ["application/json"])
class MedicineNotificationController(
    val medicineNotificationService: MedicineNotificationService,
    val usersRepository: UsersRepository
) {

    @GetMapping("/medicine-notifications", produces = ["application/json"])
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