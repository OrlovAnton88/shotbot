package ru.oav.shotbot.config

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.oav.shotbot.model.GameEvent
import java.util.concurrent.CopyOnWriteArrayList

@Component
class GameWebSocketHandler : TextWebSocketHandler() {

    val sessions = CopyOnWriteArrayList<WebSocketSession>()

    fun send(event: GameEvent) {
        sessions.forEach { if(it.isOpen) it.sendMessage(TextMessage(event.name)) }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions += session
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.removeIf { it.id == session.id }
    }
}
