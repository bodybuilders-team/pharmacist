package pt.ulisboa.ist.pharmacist.service.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler


abstract class JsonWebSocketHandler<I>(private val inputType: Class<I>) : TextWebSocketHandler() {
    private val mapper: ObjectMapper = Jackson2ObjectMapperBuilder.json().build()


    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println("Received websocket message: ${message.payload}")
        handleObject(session, mapper.readValue(message.payload, inputType))
    }

    fun <O> sendObject(session: WebSocketSession, obj: O) {
        session.sendMessage(TextMessage(mapper.writeValueAsString(obj)))
    }

    abstract fun handleObject(session: WebSocketSession, obj: I)

}