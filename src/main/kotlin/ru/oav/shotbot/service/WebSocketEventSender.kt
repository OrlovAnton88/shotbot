package ru.oav.shotbot.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import ru.oav.shotbot.model.GameEvent

@Component
class WebSocketEventSender(private val messagingTemplate: SimpMessagingTemplate) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WebSocketEventSender::class.java)
    }

    fun send(event: GameEvent) {
        val destination = "/topic/"
        logger.debug("Sending WS event: $event")
        messagingTemplate.convertAndSend(destination, event)
    }
}
