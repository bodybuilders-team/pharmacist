package pt.ulisboa.ist.pharmacist.utils

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import pt.ulisboa.ist.pharmacist.http.controllers.medicines.MedicineNotificationWebSocketHandler
import pt.ulisboa.ist.pharmacist.http.utils.Uris


@Configuration
@EnableWebSocket
class WebSocketConfiguration(
    private val medicineNotificationWebSocketHandler: MedicineNotificationWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(medicineNotificationWebSocketHandler, Uris.MEDICINE_NOTIFICATIONS)
            .setAllowedOrigins("*")
    }
}