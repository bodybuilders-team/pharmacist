package pt.ulisboa.ist.pharmacist.http.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.medicinenotifications.MedicineNotificationService
import java.util.concurrent.Executors

@RestController
@RequestMapping(produces = ["application/json"])
class MedicineNotificationController(
    val medicineNotificationService: MedicineNotificationService,
    val usersRepository: UsersRepository
) {

    data class MedicineStockAlreadyNotified(
        val medicineId: Long,
        val pharmacyId: Long,
        val medicineStock: Long,
    )

    @GetMapping("/medicine-notifications", produces = ["application/json"])
    fun streamSseMvc(): SseEmitter {
        val emitter = SseEmitter()
        val sseMvcExecutor = Executors.newSingleThreadExecutor()

        val user = usersRepository.findById(0)!!
        val processedNotied = mutableSetOf<MedicineStockAlreadyNotified>()

        sseMvcExecutor.execute {
            try {
                while (true) {
                    val notis = medicineNotificationService.findNotifications(user)

                    for (noti in notis) {
                        val previousStock =
                            processedNotied.find { it.medicineId == noti.medicineStock.medicine.id && it.pharmacyId == noti.pharmacy.id }

                        processedNotied.add(
                            MedicineStockAlreadyNotified(
                                noti.medicineStock.medicine.id,
                                noti.pharmacy.id,
                                noti.medicineStock.stock
                            )
                        )

                        if (previousStock != null && previousStock.medicineStock > 0) {
                            processedNotied.remove(previousStock)

                            continue
                        }

                        if(noti.medicineStock.stock > 0) {
                            continue
                        }

                        val event = SseEmitter.event()
                            .data(noti).build()

                        emitter.send(event)
                    }
                    Thread.sleep(1000)
                }
            } catch (ex: Exception) {
                emitter.completeWithError(ex)
            }
        }
        return emitter
    }
}