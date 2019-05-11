package com.stratix.demo.notification

import com.fasterxml.jackson.module.kotlin.readValue
import com.stratix.demo.EventData
import com.stratix.demo.WebSocketHandler
import io.tekniq.web.sparklinMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.slf4j.LoggerFactory

@WebSocket
class NotificationWebSocket : WebSocketHandler() {
    private val logger = LoggerFactory.getLogger(NotificationWebSocket::class.java)

    override fun onWebSocketMessage(session: Session, message: String) {
        val msg = sparklinMapper.readValue<EventData<String>>(message)
        logger.info("Message received: $msg")
    }
}
