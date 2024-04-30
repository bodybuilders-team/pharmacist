package pt.ulisboa.ist.pharmacist.utils

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatesWebSocketHandler
import pt.ulisboa.ist.pharmacist.http.utils.Uris


@Configuration
@EnableWebSocket
class WebSocketConfiguration(
    private val realTimeUpdatesWebSocketHandler: RealTimeUpdatesWebSocketHandler,
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(realTimeUpdatesWebSocketHandler, Uris.UPDATE_SUBSCRIPTIONS)
            .setAllowedOrigins("*")
    }
}