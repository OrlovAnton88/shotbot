package ru.oav.shotbot.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(private val gameWebSocketHandler: GameWebSocketHandler) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(gameWebSocketHandler, "/ws").setAllowedOriginPatterns("*")
    }
}
